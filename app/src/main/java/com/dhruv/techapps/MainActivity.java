package com.dhruv.techapps;
/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.dhruv.techapps.adapter.TabAdapter;
import com.dhruv.techapps.databinding.ActivityMainBinding;
import com.dhruv.techapps.fragment.MyBidsFragment;
import com.dhruv.techapps.fragment.RecentVehiclesFragment;
import com.dhruv.techapps.fragment.UserDialogFragment;
import com.dhruv.techapps.models.User;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity implements UserDialogFragment.EditNameDialogListener {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setElevation(0);

        MobileAds.initialize(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        TabAdapter adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new RecentVehiclesFragment(), "Recent");
        adapter.addFragment(new MyBidsFragment(), "My Bids");
        binding.container.setAdapter(adapter);
        binding.tabs.setupWithViewPager(binding.container);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user == null) {
                        getUserInfo();
                    } else if (user.isAdmin) {
                        binding.fabNewCar.setVisibility(View.VISIBLE);// Button launches NewPostActivity
                        binding.fabNewCar.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NewVehicleActivity.class)));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            startActivity(new Intent(this, PhoneAuthActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, PhoneAuthActivity.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void getUserInfo() {
        FragmentManager fm = getSupportFragmentManager();
        UserDialogFragment myDialogFragment = new UserDialogFragment();
        myDialogFragment.show(fm, "fragment_edit_name");
    }

    @Override
    public void onFinishEditDialog(String inputText) {
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            User user = new User();
            user.username = inputText;
            user.phone = mUser.getPhoneNumber();
            Map<String, Object> postValues = user.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            Log.d(TAG, childUpdates.toString());
            childUpdates.put("/users/" + mUser.getUid(), postValues);
            FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
        } else {
            startActivity(new Intent(this, PhoneAuthActivity.class));
            finish();
        }

    }

}
