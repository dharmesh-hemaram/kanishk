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
import com.dhruv.techapps.common.DataHolder;
import com.dhruv.techapps.databinding.ActivityCarDetailBinding;
import com.dhruv.techapps.models.Bid;
import com.dhruv.techapps.models.Car;
import com.dhruv.techapps.models.User;
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

public class CarDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_POST_KEY = "post_key";
    private static final String TAG = "PostDetailActivity";
    private DatabaseReference mCarReference;
    private DatabaseReference mBiddingsReference;
    private ValueEventListener mPostListener;
    private String mPostKey;
    private BidAdapter mAdapter;
    private ActivityCarDetailBinding binding;
    private SliderAdapterExample adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCarDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get post key from intent
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }
        Log.d(TAG, mPostKey);

        // Initialize Database
        mCarReference = FirebaseDatabase.getInstance().getReference().child("cars").child(mPostKey);
        mBiddingsReference = FirebaseDatabase.getInstance().getReference().child("car-bidding").child(mPostKey);

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
                Car car = dataSnapshot.getValue(Car.class);


                String[] name = car.brand.split(",");
                String brand = DataHolder.getInstance().getBrands()[Integer.parseInt(name[0])];
                String model = DataHolder.getInstance().getModels(Integer.parseInt(name[0]))[Integer.parseInt(name[1])];
                String variant = DataHolder.getInstance().getVariants(Integer.parseInt(name[0]),Integer.parseInt(name[1]))[Integer.parseInt(name[2])];

                binding.carTextLayout.carBrand.setText(brand);
                binding.carTextLayout.carModel.setText(model);
                binding.carTextLayout.carVariant.setText(variant);


                // [START_EXCLUDE]
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setGroupingSeparator(',');
                symbols.setDecimalSeparator('.');

                DecimalFormat currencyFormat = new DecimalFormat("₹ #,###", symbols);
                binding.carPrice.setText(currencyFormat.format(car.price));
                binding.carYear.setText(String.valueOf(car.year));


                // [END_EXCLUDE]

                Log.d(TAG, mPostKey);
                FirebaseStorage.getInstance().getReference("/images/"+mPostKey).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        Log.d(TAG,"here>>"+listResult.getItems().size());
                        if (listResult.getItems().size() > 0) {


                            final long ONE_MEGABYTE = 1024 * 1024;
                            listResult.getItems().get(0).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    Log.d(TAG,"here"+bitmap.toString());
                                    adapter.addItem(bitmap);
                                }
                            });
                        }else{
                            binding.imageSlider.findViewById(R.id.imageSlider).setVisibility(View.GONE);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(CarDetailActivity.this, "Failed to load car details.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mCarReference.addValueEventListener(postListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mPostListener = postListener;

        // Listen for comments
        mAdapter = new BidAdapter(this, mBiddingsReference);
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

    private void postBidding() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
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
                        mBiddingsReference.push().setValue(bid);

                        // Clear the field
                        binding.fieldBiddingAmount.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


}
