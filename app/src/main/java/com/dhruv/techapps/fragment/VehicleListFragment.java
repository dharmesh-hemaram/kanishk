package com.dhruv.techapps.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dhruv.techapps.R;
import com.dhruv.techapps.VehicleDetailActivity;
import com.dhruv.techapps.common.Common;
import com.dhruv.techapps.models.Vehicle;
import com.dhruv.techapps.module.GlideApp;
import com.dhruv.techapps.viewholder.VehicleViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

public abstract class VehicleListFragment extends Fragment implements AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener {

    private static final String TAG = "VehicleListFragment";
    protected String type = Common.TYPES[0];
    protected String query;
    // [END define_database_reference]
    // [START define_database_reference]
    private DatabaseReference mDatabase;
    // [END define_database_reference]

    private FirebaseRecyclerAdapter<Vehicle, VehicleViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private ProgressBar mProgressBar;

    public VehicleListFragment() {
    }

    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_vehicles, container, false);
        loadAd(rootView);
        setTypeFilter(rootView);

        ((SearchView) rootView.findViewById(R.id.search)).setOnQueryTextListener(this);
        // [START create_database_reference]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END create_database_reference]
        mProgressBar = rootView.findViewById(R.id.progressBar);
        mRecycler = rootView.findViewById(R.id.messagesList);
        mRecycler.setHasFixedSize(true);
        return rootView;
    }

    private void loadAd(View rootView) {
        /*mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());*/

        /*AdView mAdView = rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
    }

    private void setTypeFilter(View rootView) {
        AutoCompleteTextView typeFilter = rootView.findViewById(R.id.typeFilter);
        typeFilter.setText(type);
        typeFilter.setOnItemSelectedListener(this);
        typeFilter.setAdapter(new ArrayAdapter<>(requireContext(), R.layout.list_item, Common.TYPES));
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
        type = Common.TYPES[position];
        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions<Vehicle> options = new FirebaseRecyclerOptions.Builder<Vehicle>().setQuery(postsQuery, Vehicle.class).build();
        mAdapter.updateOptions(options);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        Query postsQuery = getQuery(mDatabase);

        FirebaseRecyclerOptions<Vehicle> options = new FirebaseRecyclerOptions.Builder<Vehicle>().setQuery(postsQuery, Vehicle.class).build();

        mAdapter = new FirebaseRecyclerAdapter<Vehicle, VehicleViewHolder>(options) {

            @Override
            public void onDataChanged() {

            }

            @NonNull
            @Override
            public VehicleViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                return new VehicleViewHolder(inflater.inflate(R.layout.item_vehicle, viewGroup, false));
            }

            @Override
            @NotNull
            protected void onBindViewHolder(@NonNull VehicleViewHolder viewHolder, int position, @NonNull final Vehicle model) {
                final DatabaseReference postRef = getRef(position);


                // Set click listener for the whole post view
                final String postKey = postRef.getKey();
                FirebaseStorage.getInstance().getReference("images/" + postKey).listAll().addOnSuccessListener(listResult -> {
                    if (listResult.getItems().size() > 0) {
                        GlideApp.with(requireContext()).load(listResult.getItems().get(0)).into(viewHolder.imageView);
                    }
                });
                viewHolder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), VehicleDetailActivity.class);
                    intent.putExtra(VehicleDetailActivity.EXTRA_POST_KEY, postKey);
                    intent.putExtra(VehicleDetailActivity.EXTRA_POST_TYPE, type);
                    startActivity(intent);
                });

                // Determine if the current user has liked this post and set UI accordingly
                /*if (model.stars.containsKey(getUid())) {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_24);
                } else {
                    viewHolder.starView.setImageResource(R.drawable.ic_toggle_star_outline_24);
                }*/

                // Bind Post to ViewHolder, setting OnClickListener for the star button
                /*viewHolder.bindToPost(getResources(), model, starView -> {
                    // Need to write to both places the post is stored
                    DatabaseReference globalPostRef = mDatabase.child(type).child(Objects.requireNonNull(postRef.getKey()));
                    // Run two transactions
                    onStarClicked(globalPostRef);
                });*/
            }
        };
        mRecycler.setAdapter(mAdapter);
    }

    // [START post_stars_transaction]
    private void onStarClicked(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Vehicle p = mutableData.getValue(Vehicle.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                /*if (p.stars.containsKey(getUid())) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(getUid());
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(getUid(), true);
                }*/

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot currentData) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    // [END post_stars_transaction]


    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    public abstract Query getQuery(DatabaseReference databaseReference);

}
