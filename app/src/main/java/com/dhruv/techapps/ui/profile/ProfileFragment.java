package com.dhruv.techapps.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dhruv.techapps.ProfileActivity;
import com.dhruv.techapps.R;
import com.dhruv.techapps.WelcomeActivity;
import com.dhruv.techapps.module.GlideApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.dhruv.techapps.common.Common.EXTRA_DISPLAY_NAME;
import static com.dhruv.techapps.common.Common.EXTRA_IMAGE_URI;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private static final int PROFILE = 1;
    ImageView profileImage;
    TextView profileName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

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
            String name = data.getStringExtra(EXTRA_DISPLAY_NAME);
            Uri imageUri = data.getParcelableExtra(EXTRA_IMAGE_URI);
            if (name != null) {
                profileName.setText(name);
            }
            if (imageUri != null) {
                profileImage.setImageURI(imageUri);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(requireActivity(), WelcomeActivity.class));
            requireActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }
}