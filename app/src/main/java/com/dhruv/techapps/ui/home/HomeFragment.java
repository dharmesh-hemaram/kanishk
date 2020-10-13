package com.dhruv.techapps.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dhruv.techapps.MainActivity;
import com.dhruv.techapps.NewVehicleActivity;
import com.dhruv.techapps.R;
import com.dhruv.techapps.common.Common;
import com.dhruv.techapps.models.Vehicle;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    ActionBar actionBar;
    private String type = Common.TYPES[0];
    private RecyclerView mRecycler;
    private HomeAdapter homeAdapter;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler = root.findViewById(R.id.messagesList);
        mRecycler.setHasFixedSize(true);
        progressBar = root.findViewById(R.id.progressBar);
        actionBar = ((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();
        root.findViewById(R.id.fabNewVehicle).setVisibility(View.VISIBLE);
        root.findViewById(R.id.fabNewVehicle).setOnClickListener(v -> startActivity(new Intent(getActivity(), NewVehicleActivity.class)));
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (actionBar != null) {
            actionBar.setTitle(type);
        }

        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        FirebaseRecyclerOptions<Vehicle> options = new FirebaseRecyclerOptions.Builder<Vehicle>().setQuery(postsQuery, Vehicle.class).build();
        homeAdapter = new HomeAdapter(options, type, getContext(), getActivity(), getResources());
        mRecycler.setAdapter(homeAdapter);
    }

    private void updateQuery() {
        Query postsQuery = getQuery(mDatabase);
        FirebaseRecyclerOptions<Vehicle> options = new FirebaseRecyclerOptions.Builder<Vehicle>().setQuery(postsQuery, Vehicle.class).build();
        homeAdapter.updateOptions(options);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (homeAdapter != null) {
            homeAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (homeAdapter != null) {
            homeAdapter.stopListening();
        }
    }

    public Query getQuery(DatabaseReference databaseReference) {
        Log.d(TAG, type.toLowerCase());
        return databaseReference.child(type.toLowerCase()).limitToFirst(100);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        type = item.getTitle().toString();
        if (actionBar != null) {
            actionBar.setTitle(type);
        }
        updateQuery();
        return super.onOptionsItemSelected(item);
    }
}