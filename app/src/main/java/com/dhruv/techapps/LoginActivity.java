package com.dhruv.techapps;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dhruv.techapps.databinding.ActivityLoginBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.concurrent.TimeUnit;

import static com.dhruv.techapps.common.Common.EXTRA_PHONE_NUMBER;
import static com.dhruv.techapps.common.Common.EXTRA_TOKEN;
import static com.dhruv.techapps.common.Common.EXTRA_VERIFICATION_ID;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";
    private ActivityLoginBinding mBinding;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private Resources resources;
    private boolean mVerificationInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.buttonNext.setOnClickListener(this);
        mBinding.fieldPhoneNumber.requestFocus();
        resources = getResources();
        setCallbacks();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(mBinding.fieldPhoneNumber.getText().toString());
        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mBinding.fieldPhoneNumber.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mBinding.fieldPhoneNumber.setError("Invalid phone number.");
            return false;
        }
        if (phoneNumber.length() != 10) {
            mBinding.fieldPhoneNumber.setError("Invalid phone number.");
            return false;
        }
        return true;
    }

    private void setCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                mVerificationInProgress = false;
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Intent intent = new Intent(getApplicationContext(), VerificationActivity.class);
                intent.putExtra(EXTRA_VERIFICATION_ID, verificationId);
                intent.putExtra(EXTRA_PHONE_NUMBER, getShowPhoneNumber());
                intent.putExtra(EXTRA_TOKEN, token);
                startActivity(intent);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                mVerificationInProgress = false;
                mBinding.fieldPhoneNumber.setEnabled(true);
                mBinding.buttonNext.setEnabled(true);
                mBinding.progressBarLogin.setVisibility(View.GONE);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    mBinding.fieldPhoneNumber.setError("Invalid phone number.");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.", Snackbar.LENGTH_LONG).show();
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (!validatePhoneNumber()) {
            return;
        }
        new MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.phone_verify_title))
                .setMessage(getShowPhoneNumber() + "\n\n" + resources.getString(R.string.phone_verify_message))
                .setNeutralButton(resources.getString(R.string.phone_verify_edit), (dialog, which) -> dialog.dismiss())
                .setPositiveButton(resources.getString(R.string.phone_verify_ok), (dialog, which) -> startPhoneNumberVerification(getPhoneNumber())).show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }

    public String getShowPhoneNumber() {
        return "+91 " + mBinding.fieldPhoneNumber.getText().toString();
    }

    public String getPhoneNumber() {
        return "+91" + mBinding.fieldPhoneNumber.getText().toString();
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        mBinding.fieldPhoneNumber.setEnabled(false);
        mBinding.buttonNext.setEnabled(false);
        mBinding.progressBarLogin.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, this, mCallbacks);
        mVerificationInProgress = true;
    }
}