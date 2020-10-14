package com.dhruv.techapps;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dhruv.techapps.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ProfileActivity";
    ActivityProfileBinding mBinding;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.fieldName.requestFocus();
        mBinding.buttonNext.setOnClickListener(this);
        mBinding.imageProfile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mBinding.buttonNext.getId()) {
            String name = mBinding.fieldName.getText().toString();
            if (TextUtils.isEmpty(name)) {
                mBinding.fieldName.setError("Required");
                return;
            }
            mBinding.progressBarProfile.setVisibility(View.VISIBLE);
            mBinding.buttonNext.setEnabled(false);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            UserProfileChangeRequest.Builder profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name);
            if (imageUri != null) {
                profileUpdates.setPhotoUri(imageUri);
            }
            assert user != null;
            user.updateProfile(profileUpdates.build())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    });

            Log.d(TAG, name);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                if (data.getData() != null) {
                    imageUri = data.getData();
                    mBinding.imageProfile.setImageURI(imageUri);
                }
            }
        }
    }
}