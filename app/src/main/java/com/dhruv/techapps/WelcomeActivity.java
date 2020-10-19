package com.dhruv.techapps;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dhruv.techapps.databinding.ActivityWelcomeBinding;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import static com.dhruv.techapps.common.Common.REQUEST_CODE_PROFILE;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";
    ActivityWelcomeBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        setContentView(mBinding.getRoot());
        MobileAds.initialize(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            new CountDownTimer(1000, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    if (currentUser.getDisplayName() == null || currentUser.getDisplayName().isEmpty()) {
                        startActivityForResult(new Intent(getApplicationContext(), ProfileActivity.class), REQUEST_CODE_PROFILE);
                    } else {
                        startActivity(new Intent(getApplicationContext(), LandingActivity.class));
                        finish();
                    }
                }
            }.start();

        } else {
            mBinding.textPrivacyPolicy.setVisibility(View.VISIBLE);
            mBinding.buttonContinue.setVisibility(View.VISIBLE);
            mBinding.buttonContinue.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PROFILE) {
            startActivity(new Intent(getApplicationContext(), LandingActivity.class));
            finish();
        }
    }
}