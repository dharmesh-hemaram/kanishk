package com.dhruv.techapps;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.dhruv.techapps.databinding.ActivityVerificationBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.dhruv.techapps.common.Common.EXTRA_PHONE_NUMBER;
import static com.dhruv.techapps.common.Common.EXTRA_TOKEN;
import static com.dhruv.techapps.common.Common.EXTRA_VERIFICATION_ID;
import static com.dhruv.techapps.common.Common.REQUEST_CODE_PROFILE;

public class VerificationActivity extends AppCompatActivity implements PinEntryEditText.OnPinEnteredListener, View.OnClickListener {

    private static final String TAG = "VerificationActivity";

    private ActivityVerificationBinding mBinding;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private String mPhoneNumber;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityVerificationBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        // Get post key from intent
        mPhoneNumber = getIntent().getStringExtra(EXTRA_PHONE_NUMBER);
        mVerificationId = getIntent().getStringExtra(EXTRA_VERIFICATION_ID);
        mResendToken = getIntent().getParcelableExtra(EXTRA_TOKEN);
        if (null == mPhoneNumber || null == mVerificationId || null == mResendToken) {
            throw new IllegalArgumentException("Must pass Phone number and token");
        }
        resources = getResources();
        mBinding.textPhoneNumber.setText(getResources().getString(R.string.verify_phone_number_title, mPhoneNumber));
        mBinding.textPhoneMessage.setText(getResources().getString(R.string.verify_phone_number_message, mPhoneNumber));
        mBinding.pinEntryEditText.setOnPinEnteredListener(this);
        mBinding.pinEntryEditText.requestFocus();
        mBinding.buttonResend.setOnClickListener(this);
        mBinding.buttonWrongNumber.setOnClickListener(this);
        setCallbacks();
        setResendCountDown();
    }

    private void setResendCountDown() {
        mBinding.buttonResend.setEnabled(false);
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                mBinding.textTimer.setText(new SimpleDateFormat("ss", new Locale("en", "in")).format(new Date(millisUntilFinished)));
            }

            public void onFinish() {
                mBinding.textTimer.setText("");
                mBinding.buttonResend.setEnabled(true);
            }
        }.start();
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        if (task.getResult() != null) {
                            FirebaseUser user = task.getResult().getUser();
                            if (user != null) {
                                Log.d(TAG, user.getUid());
                                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                                intent.putExtra(EXTRA_VERIFICATION_ID, user.getUid());
                                startActivityForResult(intent, REQUEST_CODE_PROFILE);
                            }
                        }
                        mBinding.progressBarVerify.setVisibility(View.GONE);
                    } else {
                        mBinding.progressBarVerify.setVisibility(View.GONE);
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            new MaterialAlertDialogBuilder(this)
                                    .setMessage("The code you entered is incorrect. Please try again in 1 minute.")
                                    .setNeutralButton(resources.getString(R.string.phone_verify_ok), (dialog, which) -> dialog.dismiss()).show();
                            mBinding.pinEntryEditText.setText("");
                            mBinding.pinEntryEditText.requestFocus();
                            // [END_EXCLUDE]
                        }
                    }
                });
    }

    @Override
    public void onPinEntered(CharSequence str) {
        mBinding.progressBarVerify.setVisibility(View.VISIBLE);
        verifyPhoneNumberWithCode(mVerificationId, str.toString());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == mBinding.buttonResend.getId()) {
            resendVerificationCode(mPhoneNumber.replaceAll("\\s+", ""), mResendToken);
        } else {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    private void setCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);

            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
                setResendCountDown();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
            }
        };
    }


    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        if (!phoneNumber.contains("+91")) {
            phoneNumber = "+91" + phoneNumber;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
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