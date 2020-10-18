package com.dhruv.techapps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.dhruv.techapps.common.Common;
import com.dhruv.techapps.databinding.ActivityLandingBinding;

import static com.dhruv.techapps.MainActivity.EXTRA_TYPE_KEY;

public class LandingActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityLandingBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityLandingBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.buttonBikes.setOnClickListener(this);
        mBinding.buttonCars.setOnClickListener(this);
        mBinding.buttonTempos.setOnClickListener(this);
        mBinding.buttonTractors.setOnClickListener(this);
        mBinding.buttonTrucks.setOnClickListener(this);
    }

    @Override

    public void onClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        if (v.getId() == R.id.buttonBikes) {
            intent.putExtra(EXTRA_TYPE_KEY, Common.TYPES[4]);
        } else if (v.getId() == R.id.buttonTrucks) {
            intent.putExtra(EXTRA_TYPE_KEY, Common.TYPES[2]);
        } else if (v.getId() == R.id.buttonTractors) {
            intent.putExtra(EXTRA_TYPE_KEY, Common.TYPES[1]);
        } else if (v.getId() == R.id.buttonTempos) {
            intent.putExtra(EXTRA_TYPE_KEY, Common.TYPES[3]);
        } else {
            intent.putExtra(EXTRA_TYPE_KEY, Common.TYPES[0]);
        }
        startActivity(intent);
    }
}