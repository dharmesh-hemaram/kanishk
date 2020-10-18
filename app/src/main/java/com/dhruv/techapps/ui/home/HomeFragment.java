package com.dhruv.techapps.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.dhruv.techapps.common.DataHolder;
import com.dhruv.techapps.models.Vehicle;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static com.dhruv.techapps.common.Common.EXTRA_VEHICLE_TYPE;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    ActionBar actionBar;
    FloatingActionButton fabNewVehicle;
    private String vehicleType;
    private RecyclerView mRecycler;
    private HomeAdapter homeAdapter;
    private DatabaseReference mDatabase;
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        vehicleType = DataHolder.getInstance().getSelectedVehicleType();
        if (vehicleType == null) {
            vehicleType = requireActivity().getIntent().getStringExtra(EXTRA_VEHICLE_TYPE);
        }
        if (vehicleType == null) {
            vehicleType = Common.TYPES[0];
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.type_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler = root.findViewById(R.id.messagesList);
        mRecycler.setHasFixedSize(true);
        progressBar = root.findViewById(R.id.progressBar);
        actionBar = ((MainActivity) requireActivity()).getSupportActionBar();
        fabNewVehicle = root.findViewById(R.id.fabNewVehicle);
        return root;
    }

    public void checkAdminAccess() {
        Log.d("ADMIN", "checkAdminAccess");
        if (DataHolder.getInstance().getIsAdmin()) {
            fabNewVehicle.setVisibility(View.VISIBLE);
            fabNewVehicle.setOnClickListener(v -> startActivity(new Intent(getActivity(), NewVehicleActivity.class)));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (actionBar != null) {
            actionBar.setTitle(vehicleType);
            actionBar.setIcon(R.drawable.ic_baseline_local_shipping_24);
        }

        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);
        FirebaseRecyclerOptions<Vehicle> options = new FirebaseRecyclerOptions.Builder<Vehicle>().setQuery(postsQuery, Vehicle.class).build();
        homeAdapter = new HomeAdapter(options, vehicleType, getContext(), getActivity(), getResources(), progressBar);
        mRecycler.setAdapter(homeAdapter);
    }

    private void updateQuery() {
        Query postsQuery = getQuery(mDatabase);
        FirebaseRecyclerOptions<Vehicle> options = new FirebaseRecyclerOptions.Builder<Vehicle>().setQuery(postsQuery, Vehicle.class).build();
        homeAdapter.vehicleType = vehicleType;
        homeAdapter.updateOptions(options);
    }

    @Override
    public void onStart() {
        super.onStart();
        checkAdminAccess();
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
        return databaseReference.child(vehicleType.toLowerCase()).orderByChild("sold").equalTo(false).limitToFirst(100);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        progressBar.setVisibility(View.VISIBLE);
        vehicleType = item.getTitle().toString();
        DataHolder.getInstance().setSelectedVehicleType(vehicleType);
        if (actionBar != null) {
            actionBar.setTitle(vehicleType);
        }
        updateQuery();
        return super.onOptionsItemSelected(item);
    }
}