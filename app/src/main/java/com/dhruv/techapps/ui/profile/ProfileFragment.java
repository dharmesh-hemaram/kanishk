package com.dhruv.techapps.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dhruv.techapps.ProfileActivity;
import com.dhruv.techapps.R;
import com.dhruv.techapps.module.GlideApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.dhruv.techapps.ProfileActivity.DISPLAY_NAME_KEY;
import static com.dhruv.techapps.ProfileActivity.IMAGE_URI_KEY;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private static final int PROFILE = 1;
    ImageView profileImage;
    TextView profileName;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        profileImage = root.findViewById(R.id.profileImage);
        profileName = root.findViewById(R.id.profileName);
        root.findViewById(R.id.buttonEdit).setOnClickListener(v -> startActivityForResult(new Intent(getContext(), ProfileActivity.class), PROFILE));
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            profileName.setText(firebaseUser.getDisplayName());
            if (firebaseUser.getPhotoUrl() != null) {
                Log.d(TAG, firebaseUser.getPhotoUrl().toString());
                GlideApp.with(this)
                        .load(firebaseUser.getPhotoUrl())
                        .into(profileImage);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PROFILE) {
            assert data != null;
            String name = data.getStringExtra(DISPLAY_NAME_KEY);
            Uri imageUri = data.getParcelableExtra(IMAGE_URI_KEY);
            if (name != null) {
                profileName.setText(name);
            }
            if (imageUri != null) {
                profileImage.setImageURI(imageUri);
            }
        }
    }
}