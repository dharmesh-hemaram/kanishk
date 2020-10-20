package com.dhruv.techapps.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dhruv.techapps.R;
import com.dhruv.techapps.module.GlideApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Objects;

public class ImageStorageViewAdapter extends RecyclerView.Adapter<ImageStorageViewAdapter.ViewHolder> {
    private static final String TAG = "ImageStorageViewAdapter";
    private final Context context;
    public List<StorageReference> storageReferences;

    public ImageStorageViewAdapter(List<StorageReference> storageReferences, Context context) {
        this.storageReferences = storageReferences;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    @NotNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_image, parent, false);
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StorageReference ref = storageReferences.get(position);
        GlideApp.with(Objects.requireNonNull(context))
                .load(ref)
                .centerCrop()
                .into(holder.imageView);
        holder.removeButton.setVisibility(View.VISIBLE);
        holder.removeButton
                .setOnClickListener(v -> FirebaseStorage.getInstance().getReference(ref.getPath()).delete()
                        .addOnSuccessListener(aVoid -> {
                            storageReferences.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context, "Image removed!", Toast.LENGTH_LONG).show();
                        })
                        .addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e)));
    }


    @Override
    public int getItemCount() {
        return storageReferences.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public Button removeButton;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.fieldImage);
            this.removeButton = itemView.findViewById(R.id.fieldImageRemove);
        }
    }
}
