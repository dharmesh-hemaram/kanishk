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

import com.dhruv.techapps.adapter.ImageViewAdapter;
import com.dhruv.techapps.common.Common;
import com.dhruv.techapps.common.DataHolder;
import com.dhruv.techapps.databinding.ActivityNewVehicleBinding;
import com.dhruv.techapps.models.Brand;
import com.dhruv.techapps.models.Vehicle;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewVehicleActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "NewCarActivity";
    private static final String REQUIRED = "Required";
    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private ActivityNewVehicleBinding binding;
    private int brandId = 0;
    private String brand;
    private String model;
    private String variant;
    private int modelId = 0;
    private int variantId = 0;
    private int engineTypeId = 0;
    private String type;
    private List<Brand> brands;
    private ImageViewAdapter imageViewAdapter;

    private DataHolder dataHolder = DataHolder.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewVehicleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference("images/");
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
                    Log.d(TAG, "getClipData");
                    imageViewAdapter = new ImageViewAdapter(data.getClipData(), getContentResolver());
                    binding.fieldImages.setLayoutManager(new GridLayoutManager(this, 3));
                    binding.fieldImages.setAdapter(imageViewAdapter); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.

                } else if (data.getData() != null) {
                    Log.d(TAG, "getData");
                    imageViewAdapter = new ImageViewAdapter(data.getData(), getContentResolver());
                    binding.fieldImages.setLayoutManager(new GridLayoutManager(this, 1));
                    binding.fieldImages.setAdapter(imageViewAdapter); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                }
            }
        }
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
        Log.d(TAG, parentView.getId() + "~" + position);
        switch (parentView.getId()) {
            case R.id.fieldType:
                type = Common.TYPES[position].toLowerCase();
                brands = Common.getBrands(position, dataHolder);
                ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, dataHolder.getBrands(brands));
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                binding.fieldBrand.setAdapter(aa);
                break;
            case R.id.fieldBrand:
                brandId = position;
                brand = brands.get(position).name;
                String[] models = dataHolder.getModels(brands, brandId);
                if (models.length == 1) {
                    binding.fieldModelText.setVisibility(View.VISIBLE);
                    binding.fieldVariantText.setVisibility(View.VISIBLE);
                    binding.fieldModel.setVisibility(View.GONE);
                    binding.fieldVariant.setVisibility(View.GONE);
                } else {
                    binding.fieldModelText.setVisibility(View.GONE);
                    binding.fieldVariantText.setVisibility(View.GONE);
                    binding.fieldModel.setVisibility(View.VISIBLE);
                    binding.fieldVariant.setVisibility(View.VISIBLE);
                    aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, dataHolder.getModels(brands, brandId));
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.fieldModel.setAdapter(aa);
                }

                break;
            case R.id.fieldModel:
                modelId = position;
                if (modelId + 1 == binding.fieldModel.getAdapter().getCount()) {
                    binding.fieldModelText.setVisibility(View.VISIBLE);
                    binding.fieldVariantText.setVisibility(View.VISIBLE);
                    binding.fieldVariant.setVisibility(View.GONE);
                    binding.fieldModelText.requestFocus();
                } else {
                    model = brands.get(brandId).models.get(position).name;
                    binding.fieldVariant.setVisibility(View.VISIBLE);
                    binding.fieldModelText.setVisibility(View.GONE);
                    aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, dataHolder.getVariants(brands, brandId, modelId));
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.fieldVariant.setAdapter(aa);
                }
                break;
            case R.id.fieldVariant:
                variantId = position;
                if (variantId + 1 == binding.fieldVariant.getAdapter().getCount()) {
                    binding.fieldVariantText.setVisibility(View.VISIBLE);
                    binding.fieldVariantText.requestFocus();
                } else {
                    variant = brands.get(brandId).models.get(modelId).variants[position];
                    binding.fieldVariantText.setVisibility(View.GONE);
                }
                break;
            case R.id.fieldEngineType:
                engineTypeId = position;
                break;
            default:
                Log.e(TAG, String.valueOf(parentView.getId()));
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setSpinner() {
        binding.fieldType.setOnItemSelectedListener(this);
        binding.fieldEngineType.setOnItemSelectedListener(this);
        binding.fieldBrand.setOnItemSelectedListener(this);
        binding.fieldVariant.setOnItemSelectedListener(this);
        binding.fieldModel.setOnItemSelectedListener(this);

        Log.d(TAG, Common.TYPES.toString());
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Common.TYPES);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        binding.fieldType.setAdapter(aa);

        aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Common.ENGINE_TYPES);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        binding.fieldEngineType.setAdapter(aa);
    }

    private void submitPost() {
        final String year = binding.fieldYear.getText().toString();
        final String regNum = binding.fieldRegistrationNumber.getText().toString();
        final String price = binding.fieldPrice.getText().toString();
        final String insurance = binding.fieldInsurance.getText().toString();
        final String kiloMeter = binding.fieldKiloMeter.getText().toString();
        final String color = binding.fieldColor.getText().toString();
        final String mobileNumber = binding.fieldMobileNumber.getText().toString();
        final String modelText = binding.fieldModelText.getText().toString();
        final String variantText = binding.fieldVariantText.getText().toString();

        if (imageViewAdapter == null) {
            Toast.makeText(this, "Please select images...", Toast.LENGTH_SHORT).show();
            return;
        }

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


        String name = brand;
        if (model == null || model.isEmpty()) {
            if (modelText.isEmpty()) {
                binding.fieldModelText.setError(REQUIRED);
                return;
            } else {
                name += modelText;
            }
        } else {
            name += model;
        }

        if (variant == null || variant.isEmpty()) {
            if (variantText.isEmpty()) {
                binding.fieldVariantText.setError(REQUIRED);
                return;
            } else {
                name += variantText;
            }
        } else {
            name += variant;
        }

        // Write new post
        writeNewPost(userId, name, year, price, regNum, kiloMeter, color, mobileNumber, insurance);

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
    private void writeNewPost(String userId, String name, String year, String price, String regNum, String km, String color, String mobile, String insurance) {
        String key = mDatabase.child(type).push().getKey();
        Vehicle vehicle = new Vehicle(userId, name, engineTypeId, year, price, regNum, km, color, mobile, insurance);
        Map<String, Object> postValues = vehicle.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        Log.d(TAG, key);
        Log.d(TAG, postValues.toString());
        childUpdates.put("/" + type + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);

        if (imageViewAdapter.uri != null) {
            Uri uri = imageViewAdapter.uri;
            mStorage.child(key + "/" + uri.getLastPathSegment()).putFile(uri);
        } else {
            ClipData clipData = imageViewAdapter.clipData;
            for (int index = 0; index < clipData.getItemCount(); index++) {
                Uri uri = clipData.getItemAt(index).getUri();
                mStorage.child(key + "/" + uri.getLastPathSegment()).putFile(uri);
            }
        }

    }
    // [END write_fan_out]
}
