package com.dhruv.techapps;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.dhruv.techapps.adapter.BidAdapter;
import com.dhruv.techapps.common.Common;
import com.dhruv.techapps.databinding.ActivityBidBinding;
import com.dhruv.techapps.models.Bid;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import static com.dhruv.techapps.common.Common.EXTRA_VEHICLE_KEY;
import static com.dhruv.techapps.common.Common.EXTRA_VEHICLE_NAME;
import static com.dhruv.techapps.common.Common.EXTRA_VEHICLE_PRICE;
import static com.dhruv.techapps.common.Common.EXTRA_VEHICLE_TYPE;

public class BidActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "BidActivity";
    ActivityBidBinding binding;
    private BidAdapter mAdapter;
    private String vehicleKey;
    private String vehicleType;
    private double vehiclePrice;
    private DatabaseReference mBiddingReference;
    private DatabaseReference mVehicleReference;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBidBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get post key from intent
        vehicleKey = getIntent().getStringExtra(EXTRA_VEHICLE_KEY);
        vehicleType = getIntent().getStringExtra(EXTRA_VEHICLE_TYPE);
        vehiclePrice = getIntent().getDoubleExtra(EXTRA_VEHICLE_PRICE, 0);
        String vehicleName = getIntent().getStringExtra(EXTRA_VEHICLE_NAME);
        if (null == vehicleKey || null == vehicleType) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }
        String mPostBiddingType = vehicleType.substring(0, vehicleType.length() - 1) + "-bidding";

        mBiddingReference = FirebaseDatabase.getInstance().getReference().child(mPostBiddingType.toLowerCase()).child(vehicleKey);
        mVehicleReference = FirebaseDatabase.getInstance().getReference().child(vehicleType.toLowerCase()).child(vehicleKey);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        binding.recyclerVehicleBids.setLayoutManager(mLayoutManager);
        binding.buttonVehicleBidding.setOnClickListener(this);
        binding.textName.setText(vehicleName);
        binding.textPrice.setText(Common.formatCurrency(vehiclePrice));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Listen for comments
        mAdapter = new BidAdapter(this, mBiddingReference);
        binding.recyclerVehicleBids.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonVehicleBidding) {
            postBidding();
        }
    }

    private Double getMaxPrice() {
        if (mAdapter.mBids.size() > 0) {
            Double maxPrice = 0d;
            for (int i = 0; i < mAdapter.mBids.size(); i++) {
                if (Double.compare(mAdapter.mBids.get(i).amount, maxPrice) > 0) {
                    maxPrice = mAdapter.mBids.get(i).amount;
                }
            }
            return maxPrice;
        } else {
            return vehiclePrice;
        }
    }

    private void postBidding() {
        final String uid = getUid();
        double biddingAmount = Double.parseDouble(binding.fieldBiddingAmount.getText().toString());
        double maxPrice = getMaxPrice();
        if (Double.compare(biddingAmount, maxPrice) > 0) {
            String authorName = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
            Bid bid = new Bid(uid, authorName, biddingAmount);
            // Push the comment, it will appear in the list
            mBiddingReference.push().setValue(bid);
            mVehicleReference.child("bid").setValue(bid.amount);
            // Clear the field
            binding.fieldBiddingAmount.setText(null);
        } else {
            Toast.makeText(this, "Bidding price should be higher", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            // Clean up comments listener
            mAdapter.cleanupListener();
        }
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }
}