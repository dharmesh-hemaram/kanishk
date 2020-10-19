package com.dhruv.techapps;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dhruv.techapps.adapter.SliderAdapterExample;
import com.dhruv.techapps.common.Common;
import com.dhruv.techapps.common.DataHolder;
import com.dhruv.techapps.databinding.ActivityVehicleDetailBinding;
import com.dhruv.techapps.models.Vehicle;
import com.google.android.gms.ads.AdRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.smarteist.autoimageslider.SliderView;

import static com.dhruv.techapps.common.Common.EXTRA_VEHICLE_KEY;
import static com.dhruv.techapps.common.Common.EXTRA_VEHICLE_NAME;
import static com.dhruv.techapps.common.Common.EXTRA_VEHICLE_PRICE;
import static com.dhruv.techapps.common.Common.EXTRA_VEHICLE_TYPE;

public class VehicleDetailActivity extends BaseActivity {


    private static final String TAG = "VehicleDetailActivity";
    private DatabaseReference mVehicleReference;

    private ValueEventListener mPostListener;
    private String vehicleKey;
    private String vehicleType;

    private ActivityVehicleDetailBinding binding;
    private SliderAdapterExample adapter;
    private Vehicle vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVehicleDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get post key from intent
        vehicleKey = getIntent().getStringExtra(EXTRA_VEHICLE_KEY);
        vehicleType = getIntent().getStringExtra(EXTRA_VEHICLE_TYPE);
        if (null == vehicleKey || null == vehicleType) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }
        // Initialize Database
        mVehicleReference = FirebaseDatabase.getInstance().getReference().child(vehicleType.toLowerCase()).child(vehicleKey);

        SliderView sliderView = binding.imageSlider.findViewById(R.id.imageSlider);
        adapter = new SliderAdapterExample(this);
        sliderView.setSliderAdapter(adapter);
        binding.buttonViewBids.setOnClickListener(this::viewBids);
        binding.fabShare.setOnClickListener(this::share);

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
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
                    binding.textName.setText(vehicle.name);
                    if (vehicle.bid <= 0) {
                        binding.textPrice.setText(Common.formatCurrency(vehicle.price));
                    } else {
                        binding.textPrice.setText(Common.formatCurrency(vehicle.bid));
                    }
                    binding.textYear.setText(String.valueOf(vehicle.year));
                    binding.textRegNum.setText(vehicle.reg);
                    binding.textKm.setText(Common.formatDecimal(vehicle.km));
                    binding.textEngineType.setText(vehicle.getEngineTypeName());
                    binding.textInsurance.setText(vehicle.ins);
                    binding.textColor.setText(vehicle.color);
                    binding.textMobileNumber.setText(vehicle.mobile);
                    binding.textLocation.setText(vehicle.loc);
                    binding.textStatus.setText(vehicle.getStatusName());

                    String documents = "";
                    if (vehicle.rc) {
                        documents += "RC";
                    }
                    if (vehicle.form35) {
                        if (!TextUtils.isEmpty(documents)) {
                            documents += ", ";
                        }
                        documents += "Form 35";
                    }
                    if (vehicle.form36) {
                        if (!TextUtils.isEmpty(documents)) {
                            documents += ", ";
                        }
                        documents += "Form 36";
                    }
                    binding.textDocuments.setText(documents);
                    FirebaseStorage.getInstance().getReference("/images/" + vehicleKey).listAll().addOnSuccessListener(listResult -> adapter.renewItems(listResult.getItems()));
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
    }

    private void checkAdmin() {
        if (DataHolder.getInstance().getIsAdmin()) {
            binding.buttonEdit.setVisibility(View.VISIBLE);
            binding.buttonEdit.setOnClickListener(this::onEditClick);
            //binding.textMobileNumber.setVisibility(View.VISIBLE);
            binding.buttonSoldOut.setVisibility(View.VISIBLE);
            binding.buttonSoldOut.setOnClickListener(this::onSoldOut);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mPostListener != null) {
            mVehicleReference.removeEventListener(mPostListener);
        }
    }

    private void onEditClick(View v) {
        Intent intent = new Intent(this, NewVehicleActivity.class);
        intent.putExtra(EXTRA_VEHICLE_KEY, vehicleKey);
        intent.putExtra(EXTRA_VEHICLE_TYPE, vehicleType);
        startActivity(intent);
    }

    private void viewBids(View v) {
        Intent intent = new Intent(this, BidActivity.class);
        intent.putExtra(EXTRA_VEHICLE_KEY, vehicleKey);
        intent.putExtra(EXTRA_VEHICLE_TYPE, vehicleType);
        intent.putExtra(EXTRA_VEHICLE_PRICE, vehicle.price);
        intent.putExtra(EXTRA_VEHICLE_NAME, vehicle.name);
        startActivity(intent);
    }

    private void share(View v) {

        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, formatMessage());
        whatsappIntent.setType("text/plain");
        whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
            Toast.makeText(this, "Whatsapp have not been installed.", Toast.LENGTH_LONG).show();
        }
    }

    private String formatMessage() {
        return getResources().getString(R.string.message, vehicle.name, Common.formatCurrency(vehicle.price), Common.formatCurrency(vehicle.bid), vehicle.reg, vehicle.year, Common.formatDecimal(vehicle.km), vehicle.getEngineTypeName(), vehicle.getStatusName(), vehicle.ins, vehicle.loc, vehicle.color, vehicle.getRC(), vehicle.getForm35(), vehicle.getForm36());
    }

    private void onSoldOut(View v) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Are you sure?")
                .setMessage(vehicle.name + "\n\nVehicle is sold out")
                .setNeutralButton("NO", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Yes", (dialog, which) -> {
                    mVehicleReference.child("sold").setValue(true);
                    finish();
                }).show();
    }

}



