package com.dhruv.techapps.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.dhruv.techapps.R;
import com.dhruv.techapps.VehicleDetailActivity;
import com.dhruv.techapps.models.Vehicle;
import com.dhruv.techapps.module.GlideApp;
import com.dhruv.techapps.viewholder.VehicleViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

public class HomeAdapter extends FirebaseRecyclerAdapter<Vehicle, VehicleViewHolder> {

    private static final String TAG = "HomeAdapter";
    private final Context context;
    private final FragmentActivity activity;
    private final Resources resources;
    private final ProgressBar progressBar;
    String mPostType;

    public HomeAdapter(FirebaseRecyclerOptions<Vehicle> options, String type, Context context, FragmentActivity activity, Resources resources, ProgressBar progressBar) {
        super(options);
        this.mPostType = type;
        this.context = context;
        this.activity = activity;
        this.resources = resources;
        this.progressBar = progressBar;
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        progressBar.setVisibility(View.GONE);
    }


    protected boolean filterCondition(Vehicle vehicle, String filterPattern) {
        return vehicle.name.toLowerCase().contains(filterPattern);
    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return new VehicleViewHolder(inflater.inflate(R.layout.item_vehicle, viewGroup, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull VehicleViewHolder viewHolder, int position, @NonNull final Vehicle model) {
        final DatabaseReference postRef = getRef(position);
        final String postKey = postRef.getKey();
        FirebaseStorage.getInstance().getReference("images/" + postKey).listAll().addOnSuccessListener(listResult -> {
            if (listResult.getItems().size() > 0) {
                GlideApp.with(Objects.requireNonNull(context)).load(listResult.getItems().get(0)).into(viewHolder.imageView);
            }
        });
        viewHolder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, VehicleDetailActivity.class);

            Log.d(TAG, postKey + "!!!" + mPostType);
            intent.putExtra(VehicleDetailActivity.EXTRA_POST_KEY, postKey);
            intent.putExtra(VehicleDetailActivity.EXTRA_POST_TYPE, mPostType);
            activity.startActivity(intent);
        });
        viewHolder.bindToPost(resources, model);
    }
}
