package com.dhruv.techapps;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.dhruv.techapps.adapter.ImageViewAdapter;
import com.dhruv.techapps.common.Common;
import com.dhruv.techapps.common.DataHolder;
import com.dhruv.techapps.databinding.ActivityNewVehicleBinding;
import com.dhruv.techapps.models.Brand;
import com.dhruv.techapps.models.Vehicle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.dhruv.techapps.VehicleDetailActivity.EXTRA_POST_KEY;
import static com.dhruv.techapps.VehicleDetailActivity.EXTRA_POST_TYPE;

public class NewVehicleActivity extends BaseActivity {

    private static final String TAG = "NewVehicleActivity";
    private static final String REQUIRED = "Required";
    private static final String INVALID = "Invalid";
    private ActivityNewVehicleBinding binding;
    private int brandId = 0;
    private int modelId = 0;
    private int engineTypeId = 0;
    private String type;
    private List<Brand> brands;
    private ImageViewAdapter imageViewAdapter;
    private DataHolder dataHolder = DataHolder.getInstance();

    private String mPostKey;
    private String mPostType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewVehicleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get post key from intent
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        mPostType = getIntent().getStringExtra(EXTRA_POST_TYPE);


        binding.fabSubmitPost.setOnClickListener(v -> submitPost());
        binding.fieldImageSelect.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        });
        setTypes();
        setBrands();
        setModels();
        setVariants();
        setEngineTypes();
        setNumberFormatter();
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPost();
        checkPostType();
    }

    private void checkPost() {
        if (mPostKey != null) {
            DatabaseReference mVehicleReference = FirebaseDatabase.getInstance().getReference().child(mPostType.toLowerCase()).child(mPostKey);
            mVehicleReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Get Post object and use the values to update the UI
                    Vehicle vehicle = dataSnapshot.getValue(Vehicle.class);

                    if (vehicle != null) {

                        String[] names = vehicle.name.split(getResources().getString(R.string.dot));
                        // [START_EXCLUDE]
                        binding.fieldBrand.setText(names[0]);
                        binding.fieldModel.setText(names[1]);
                        binding.fieldVariant.setText(names[2]);
                        Objects.requireNonNull(binding.fieldPrice.getEditText()).setText(Common.formatCurrency(vehicle.price));
                        Objects.requireNonNull(binding.fieldYear.getEditText()).setText(String.valueOf(vehicle.year));
                        Objects.requireNonNull(binding.fieldRegistrationNumber.getEditText()).setText(vehicle.reg);
                        Objects.requireNonNull(binding.fieldKiloMeter.getEditText()).setText(Common.formatDecimal(vehicle.km));
                        binding.fieldEngineType.setText(Common.ENGINE_TYPES[vehicle.eType]);
                        Objects.requireNonNull(binding.fieldInsurance.getEditText()).setText(vehicle.ins);
                        Objects.requireNonNull(binding.fieldColor.getEditText()).setText(vehicle.color);
                        Objects.requireNonNull(binding.fieldMobileNumber.getEditText()).setText(vehicle.mobile);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void checkPostType() {
        if (mPostType != null) {
            binding.fieldType.setText(mPostType);
            int position = Arrays.asList(Common.TYPES).indexOf(mPostType);
            if (position != -1) {
                binding.fieldType.setEnabled(false);
                binding.fieldTypeLayout.setEnabled(false);
                type = Common.TYPES[position].toLowerCase();
                brands = Common.getBrands(position, dataHolder);
                binding.fieldBrand.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, dataHolder.getBrands(brands)));
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {
                    imageViewAdapter = new ImageViewAdapter(data.getClipData(), getContentResolver());
                    binding.fieldImages.setLayoutManager(new GridLayoutManager(this, 3));
                    binding.fieldImages.setAdapter(imageViewAdapter); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.

                } else if (data.getData() != null) {
                    imageViewAdapter = new ImageViewAdapter(data.getData(), getContentResolver());
                    binding.fieldImages.setLayoutManager(new GridLayoutManager(this, 1));
                    binding.fieldImages.setAdapter(imageViewAdapter); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                }
            }
        }
    }

    private void setNumberFormatter() {
        Objects.requireNonNull(binding.fieldPrice.getEditText()).setOnFocusChangeListener((v, hasFocus) -> {
            EditText editText = (EditText) v;
            String value = editText.getText().toString();
            if (!value.isEmpty()) {
                if (hasFocus) {
                    try {
                        String cleanString = Common.removeCurrencyFormatter(value);
                        editText.setText(cleanString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    String formatted = Common.formatCurrency(value);
                    editText.setText(formatted);
                }
            }
        });
        Objects.requireNonNull(binding.fieldKiloMeter.getEditText()).setOnFocusChangeListener((v, hasFocus) -> {
            EditText editText = (EditText) v;
            String value = editText.getText().toString();
            if (!value.isEmpty()) {
                if (hasFocus) {
                    try {
                        String cleanString = Common.removeDecimalFormatter(value);
                        editText.setText(cleanString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    String formatted = Common.formatDecimal(value);
                    editText.setText(formatted);
                }
            }
        });
    }

    private void setTypes() {
        binding.fieldType.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, Common.TYPES));
        binding.fieldType.setOnItemClickListener((parent, view, position, id) -> {
            type = Common.TYPES[position].toLowerCase();
            brands = Common.getBrands(position, dataHolder);
            binding.fieldBrand.setText("");
            binding.fieldModel.setText("");
            binding.fieldVariant.setText("");
            binding.fieldBrand.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, dataHolder.getBrands(brands)));
        });
    }

    private void setBrands() {
        binding.fieldBrand.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, new ArrayList<>()));
        binding.fieldBrand.setOnItemClickListener((parent, view, position, id) -> {
            brandId = position;
            String[] models = dataHolder.getModels(brands, position);
            binding.fieldModel.setText("");
            binding.fieldVariant.setText("");
            if (models != null && models.length > 0) {
                binding.fieldModel.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, models));
            } else {
                binding.fieldModel.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, new ArrayList<>()));
            }
            binding.fieldVariant.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, new ArrayList<>()));
        });
    }

    private void setModels() {
        binding.fieldModel.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, new ArrayList<>()));
        binding.fieldModel.setOnItemClickListener((parent, view, position, id) -> {
            binding.fieldVariant.setText("");
            String[] variants = dataHolder.getVariants(brands, brandId, modelId);
            if (variants != null && variants.length > 0) {
                binding.fieldVariant.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, variants));
            } else {
                binding.fieldVariant.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, new ArrayList<>()));
            }
        });
    }

    private void setVariants() {
        binding.fieldVariant.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, new ArrayList<>()));
    }

    private void setEngineTypes() {
        binding.fieldEngineType.setAdapter(new ArrayAdapter<>(this, R.layout.list_item, Common.ENGINE_TYPES));
        binding.fieldEngineType.setOnItemClickListener((parent, view, position, id) -> engineTypeId = position);
    }

    private void submitPost() {
        try {
            final String type = binding.fieldType.getText().toString();
            final String brand = binding.fieldBrand.getText().toString();
            final String model = binding.fieldModel.getText().toString();
            final String variant = binding.fieldVariant.getText().toString();
            final String engineType = binding.fieldEngineType.getText().toString();


            final String year = Objects.requireNonNull(binding.fieldYear.getEditText()).getText().toString();
            final String regNum = Objects.requireNonNull(binding.fieldRegistrationNumber.getEditText()).getText().toString();
            final String insurance = Objects.requireNonNull(binding.fieldInsurance.getEditText()).getText().toString();
            final String price = Objects.requireNonNull(binding.fieldPrice.getEditText()).getText().toString();
            final String kiloMeter = Objects.requireNonNull(binding.fieldKiloMeter.getEditText()).getText().toString();
            final String color = Objects.requireNonNull(binding.fieldColor.getEditText()).getText().toString();
            final String mobileNumber = Objects.requireNonNull(binding.fieldMobileNumber.getEditText()).getText().toString();


            if (imageViewAdapter == null && mPostKey == null) {
                binding.fieldImageSelect.requestFocus();
                Toast.makeText(this, "Please select images...", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean isError = false;

            if (TextUtils.isEmpty(type)) {
                binding.fieldType.setError(REQUIRED);
                binding.fieldType.requestFocus();
                isError = true;
            } else if (!Arrays.asList(Common.TYPES).contains(type)) {
                binding.fieldType.setError("Not supported type");
                binding.fieldType.requestFocus();
                isError = true;
            }

            if (TextUtils.isEmpty(price)) {
                binding.fieldPrice.setError(REQUIRED);
                binding.fieldPrice.requestFocus();
                isError = true;
            }

            if (TextUtils.isEmpty(year)) {
                binding.fieldYear.setError(REQUIRED);
                binding.fieldYear.requestFocus();
                isError = true;
            }

            if (TextUtils.isEmpty(regNum)) {
                binding.fieldRegistrationNumber.setError(REQUIRED);
                binding.fieldRegistrationNumber.requestFocus();
                isError = true;
            }

            if (TextUtils.isEmpty(brand)) {
                binding.fieldBrand.setError(REQUIRED);
                binding.fieldBrand.requestFocus();
                isError = true;
            }
            if (TextUtils.isEmpty(model)) {
                binding.fieldModel.setError(REQUIRED);
                binding.fieldModel.requestFocus();
                isError = true;
            }
            if (TextUtils.isEmpty(variant)) {
                binding.fieldVariant.setError(REQUIRED);
                binding.fieldVariant.requestFocus();
                isError = true;
            }
            if (TextUtils.isEmpty(engineType)) {
                binding.fieldEngineType.setError(REQUIRED);
                binding.fieldEngineType.requestFocus();
                isError = true;
            }

            if (isError) {
                return;
            }

            String name = getResources().getString(R.string.vehicle_name, brand, model, variant);

            // Disable button so there are no multi-posts
            setEditingEnabled(false);
            Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();
            // [START single_value_read]
            final String userId = getUid();
            // Write new post
            writeNewPost(userId, name, year, price, regNum, kiloMeter, color, mobileNumber, insurance);

            // Finish this Activity, back to the stream
            setEditingEnabled(true);
            finish();
            // [END_EXCLUDE]
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

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
    private void writeNewPost(String userId, String name, String year, String price, String regNum, String km, String color, String mobile, String insurance) throws ParseException {

        Vehicle vehicle = new Vehicle(userId, name, engineTypeId, year, price, regNum, km, color, mobile, insurance);
        Map<String, Object> postValues = vehicle.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        // [START initialize_database_ref]
        // [START declare_database_ref]
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        StorageReference mStorage = FirebaseStorage.getInstance().getReference("images/");
        // [END initialize_database_ref]
        if (mPostKey == null) {
            mPostKey = mDatabase.child(type).push().getKey();
        }
        childUpdates.put("/" + type + "/" + mPostKey, postValues);

        mDatabase.updateChildren(childUpdates);
        if (imageViewAdapter != null) {
            if (imageViewAdapter.uri != null) {
                Uri uri = imageViewAdapter.uri;
                mStorage.child(mPostKey + "/" + uri.getLastPathSegment()).putFile(uri);
            } else {
                ClipData clipData = imageViewAdapter.clipData;
                for (int index = 0; index < clipData.getItemCount(); index++) {
                    Uri uri = clipData.getItemAt(index).getUri();
                    mStorage.child(mPostKey + "/" + uri.getLastPathSegment()).putFile(uri);
                }
            }
        }


    }
}
