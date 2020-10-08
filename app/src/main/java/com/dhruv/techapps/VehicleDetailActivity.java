package com.dhruv.techapps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.dhruv.techapps.adapter.BidAdapter;
import com.dhruv.techapps.adapter.SliderAdapterExample;
import com.dhruv.techapps.common.Common;
import com.dhruv.techapps.common.DataHolder;
import com.dhruv.techapps.databinding.ActivityVehicleDetailBinding;
import com.dhruv.techapps.models.Bid;
import com.dhruv.techapps.models.User;
import com.dhruv.techapps.models.Vehicle;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.smarteist.autoimageslider.SliderView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class VehicleDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_POST_KEY = "post_key";
    private static final String TAG = "VehicleDetailActivity";
    private DatabaseReference mCarReference;
    private DatabaseReference mBiddingReference;
    private ValueEventListener mPostListener;
    private String mPostKey;
    private BidAdapter mAdapter;
    private ActivityVehicleDetailBinding binding;
    private SliderAdapterExample adapter;
    private DataHolder dataHolder;
    private Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVehicleDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get post key from intent
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }
        Log.d(TAG, mPostKey);

        // Initialize Database
        mCarReference = FirebaseDatabase.getInstance().getReference().child("cars").child(mPostKey);
        mBiddingReference = FirebaseDatabase.getInstance().getReference().child("car-bidding").child(mPostKey);

        binding.buttonCarBidding.setOnClickListener(this);
        binding.recyclerCarBids.setLayoutManager(new LinearLayoutManager(this));

        SliderView sliderView = binding.imageSlider.findViewById(R.id.imageSlider);
        adapter = new SliderAdapterExample(this);
        sliderView.setSliderAdapter(adapter);

    }

    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String carKey = dataSnapshot.getKey();
                vehicle = dataSnapshot.getValue(Vehicle.class);

                setTitle(vehicle.name);

                // [START_EXCLUDE]
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator(',');
                symbols.setDecimalSeparator('.');

                DecimalFormat currencyFormat = new DecimalFormat("₹ #,###", symbols);
                DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);
                binding.carPrice.setText(currencyFormat.format(vehicle.price));
                binding.carYear.setText(String.valueOf(vehicle.year));
                binding.carRegistrationNumber.setText(vehicle.reg);
                binding.carKM.setText(decimalFormat.format(vehicle.km));
                binding.carEngineType.setText(Common.ENGINE_TYPES[vehicle.eType]);
                binding.carIns.setText(vehicle.ins);
                binding.carColor.setText(vehicle.color);

                // [END_EXCLUDE]

                FirebaseStorage.getInstance().getReference("/images/" + mPostKey).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        if (listResult.getItems().size() > 0) {
                            final long ONE_MEGABYTE = 1024 * 1024;
                            for (int i = 0; i < listResult.getItems().size(); i++) {
                                listResult.getItems().get(i).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        Log.d(TAG, "here" + bitmap.toString());
                                        adapter.addItem(bitmap);
                                    }
                                });
                            }
                        }
                    }
                });
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
        mCarReference.addValueEventListener(postListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mPostListener = postListener;

        // Listen for comments
        mAdapter = new BidAdapter(this, mBiddingReference);
        binding.recyclerCarBids.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mPostListener != null) {
            mCarReference.removeEventListener(mPostListener);
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
        Double biddingAmount = Double.parseDouble(binding.fieldBiddingAmount.getText().toString());
        Double maxPrice = getMaxPrice();
        if (Double.compare(biddingAmount, maxPrice) > 0) {
            FirebaseDatabase
                    .getInstance()
                    .getReference()
                    .child("users")
                    .child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user information
                            User user = dataSnapshot.getValue(User.class);
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
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        } else {
            Log.d(TAG, "Bidding error");
            Toast.makeText(this, "Bidding price should be higher", Toast.LENGTH_LONG).show();
        }

    }


}



