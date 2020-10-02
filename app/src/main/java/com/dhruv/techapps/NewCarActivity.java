package com.dhruv.techapps;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.dhruv.techapps.adapter.ImageViewAdapter;
import com.dhruv.techapps.databinding.ActivityNewCarBinding;
import com.dhruv.techapps.models.Car;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class NewCarActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";
    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    private ActivityNewCarBinding binding;
    private int brandId = 0;
    private int modelId = 0;
    private int variantId = 0;
    private int typeId = 0;
    private ImageViewAdapter imageViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewCarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]

        binding.fabSubmitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
        binding.fieldImageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
        setSpinner();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {
                    Log.d(TAG,"getClipData");
                    imageViewAdapter = new ImageViewAdapter(data.getClipData());
                    binding.fieldImages.setLayoutManager(new GridLayoutManager(this , 3));
                    binding.fieldImages.setAdapter(imageViewAdapter); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.

                } else if (data.getData() != null) {
                    Log.d(TAG,"getData");
                    String imagePath = data.getData().getPath();
                    //do something with the image (save it to some directory or whatever you need to do with it here)
                }
            }
        }
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
        switch (parentView.getId()) {
            case R.id.fieldBrand:
                brandId = position;
                break;
            case R.id.fieldModel:
                modelId = position;
                break;
            case R.id.fieldVariant:
                variantId = position;
                break;
            case R.id.fieldType:
                typeId = position;
                break;
            default:
                Log.e(TAG, String.valueOf(parentView.getId()));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setSpinner() {
        binding.fieldBrand.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Common.BRANDS);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        binding.fieldBrand.setAdapter(aa);


        binding.fieldModel.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the country list
        aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Common.MODELS);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        binding.fieldModel.setAdapter(aa);

        binding.fieldVariant.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the country list
        aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Common.VARIANTS);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        binding.fieldVariant.setAdapter(aa);

        binding.fieldType.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the country list
        aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Common.TYPES);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        binding.fieldType.setAdapter(aa);
    }

    private void submitPost() {
        final String year = binding.fieldYear.getText().toString();
        final String regNum = binding.fieldRegistrationNumber.getText().toString();
        final String price = binding.fieldPrice.getText().toString();
        final String insurance = binding.fieldInsurance.getText().toString();
        final String kiloMeter = binding.fieldKiloMeter.getText().toString();
        final String color = binding.fieldColor.getText().toString();
        final String mobileNumber = binding.fieldMobileNumber.getText().toString();
        final String owners = "1";

        // Title is required
        if (TextUtils.isEmpty(year)) {
            binding.fieldYear.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(regNum)) {
            binding.fieldRegistrationNumber.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(price)) {
            binding.fieldPrice.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();

        // Write new post
        writeNewPost(userId, year, price, regNum, kiloMeter, owners, color, mobileNumber, insurance);

        // Finish this Activity, back to the stream
        setEditingEnabled(true);
        finish();
        // [END_EXCLUDE]

    }

    private void setEditingEnabled(boolean enabled) {
        binding.fieldYear.setEnabled(enabled);
        binding.fieldRegistrationNumber.setEnabled(enabled);
        if (enabled) {
            binding.fabSubmitPost.show();
        } else {
            binding.fabSubmitPost.hide();
        }
    }

    // [START write_fan_out]
    private void writeNewPost(String userId, String year, String price, String regNum, String km, String owners, String color, String mobile, String insurance) {
        String key = mDatabase.child("cars").push().getKey();
        Car car = new Car(userId, brandId, modelId, variantId, year, price, regNum, km, owners, color, typeId, mobile, insurance);
        Map<String, Object> postValues = car.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        Log.d(TAG, key);
        Log.d(TAG, postValues.toString());
        childUpdates.put("/cars/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }
    // [END write_fan_out]
}
