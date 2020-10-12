package com.dhruv.techapps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dhruv.techapps.adapter.BidAdapter;
import com.dhruv.techapps.adapter.SliderAdapterExample;
import com.dhruv.techapps.common.Common;
import com.dhruv.techapps.common.DataHolder;
import com.dhruv.techapps.databinding.ActivityVehicleDetailBinding;
import com.dhruv.techapps.models.Bid;
import com.dhruv.techapps.models.User;
import com.dhruv.techapps.models.Vehicle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.smarteist.autoimageslider.SliderView;

public class VehicleDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_POST_KEY = "post_key";
    public static final String EXTRA_POST_TYPE = "post_type";
    private static final String TAG = "VehicleDetailActivity";
    private DatabaseReference mVehicleReference;
    private DatabaseReference mBiddingReference;
    private ValueEventListener mPostListener;
    private String mPostKey;
    private String mPostType;
    private BidAdapter mAdapter;
    private ActivityVehicleDetailBinding binding;
    private SliderAdapterExample adapter;
    private Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVehicleDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get post key from intent
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        mPostType = getIntent().getStringExtra(EXTRA_POST_TYPE);
        assert mPostType != null;
        String mPostBiddingType = mPostType.substring(0, mPostType.length() - 1) + "-bidding";
        if (null == mPostKey) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mVehicleReference = FirebaseDatabase.getInstance().getReference().child(mPostType.toLowerCase()).child(mPostKey);
        mBiddingReference = FirebaseDatabase.getInstance().getReference().child(mPostBiddingType.toLowerCase()).child(mPostKey);

        binding.buttonCarBidding.setOnClickListener(this);
        binding.recyclerCarBids.setLayoutManager(new LinearLayoutManager(this));

        SliderView sliderView = binding.imageSlider.findViewById(R.id.imageSlider);
        adapter = new SliderAdapterExample(this);
        sliderView.setSliderAdapter(adapter);

    }


    @Override
    public void onStart() {
        super.onStart();

        checkAdmin();

        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                vehicle = dataSnapshot.getValue(Vehicle.class);

                if (vehicle != null) {
                    setTitle(vehicle.name);
                    // [START_EXCLUDE]
                    binding.price.setText(Common.formatCurrency(vehicle.price));
                    binding.year.setText(String.valueOf(vehicle.year));
                    binding.regNum.setText(vehicle.reg);
                    binding.km.setText(Common.formatDecimal(vehicle.km));
                    binding.engineType.setText(Common.ENGINE_TYPES[vehicle.eType]);
                    binding.ins.setText(vehicle.ins);
                    binding.color.setText(vehicle.color);
                    binding.mobileNumber.setText(vehicle.mobile);

                    // [END_EXCLUDE]

                    FirebaseStorage.getInstance().getReference("/images/" + mPostKey).listAll().addOnSuccessListener(listResult -> adapter.renewItems(listResult.getItems()));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(VehicleDetailActivity.this, "Failed to load car details.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mVehicleReference.addValueEventListener(postListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mPostListener = postListener;

        // Listen for comments
        mAdapter = new BidAdapter(this, mBiddingReference);
        binding.recyclerCarBids.setAdapter(mAdapter);
    }

    private void checkAdmin() {
        if (DataHolder.getInstance().getIsAdmin()) {
            binding.fabEdit.setVisibility(View.VISIBLE);
            binding.mobileComponent.setVisibility(View.VISIBLE);
            binding.fabEdit.setOnClickListener(this::onEditClick);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mPostListener != null) {
            mVehicleReference.removeEventListener(mPostListener);
        }
        if (mAdapter != null) {
            // Clean up comments listener
            mAdapter.cleanupListener();
        }

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonCarBidding) {
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
            return vehicle.price;
        }
    }

    private void postBidding() {
        final String uid = getUid();
        double biddingAmount = Double.parseDouble(binding.fieldBiddingAmount.getText().toString());
        double maxPrice = getMaxPrice();
        if (Double.compare(biddingAmount, maxPrice) > 0) {
            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("users")
                    .child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Get user information
                            User user = dataSnapshot.getValue(User.class);
                            assert user != null;
                            String authorName = user.username;
                            // Create new comment object
                            Double biddingAmount = Double.parseDouble(binding.fieldBiddingAmount.getText().toString());
                            Bid bid = new Bid(uid, authorName, biddingAmount);

                            // Push the comment, it will appear in the list
                            mBiddingReference.push().setValue(bid);

                            // Clear the field
                            binding.fieldBiddingAmount.setText(null);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        } else {
            Log.d(TAG, "Bidding error");
            Toast.makeText(this, "Bidding price should be higher", Toast.LENGTH_LONG).show();
        }

    }


    private void onEditClick(View v) {
        Intent intent = new Intent(this, NewVehicleActivity.class);
        intent.putExtra(VehicleDetailActivity.EXTRA_POST_KEY, mPostKey);
        intent.putExtra(VehicleDetailActivity.EXTRA_POST_TYPE, mPostType);
        startActivity(intent);
    }
}



