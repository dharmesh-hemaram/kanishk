package com.dhruv.techapps;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.dhruv.techapps.databinding.ActivityWelcomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";
    ActivityWelcomeBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

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
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    } else {
                        startActivity(new Intent(getApplicationContext(), LandingActivity.class));
                    }
                }
            }.start();

        } else {
            mBinding.textPrivacyPolicy.setVisibility(View.VISIBLE);
            mBinding.buttonContinue.setVisibility(View.VISIBLE);
            mBinding.buttonContinue.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));
        }
    }
}