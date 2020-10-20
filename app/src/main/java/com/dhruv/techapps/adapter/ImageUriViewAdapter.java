package com.dhruv.techapps.adapter;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dhruv.techapps.R;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageUriViewAdapter extends RecyclerView.Adapter<ImageUriViewAdapter.ViewHolder> {
    private static final String TAG = "ImageUriViewAdapter";
    public List<Uri> uris = new ArrayList<>();
    public String mVehicleKey;
    ContentResolver contentResolver;
    StorageReference mStorage = FirebaseStorage.getInstance().getReference("images/");

    public ImageUriViewAdapter(String mVehicleKey, ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
        this.mVehicleKey = mVehicleKey;
    }

    public void deleteItem(int position) {
        this.uris.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(Uri uri) {
        this.uris.add(uri);
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
        try {
            Uri uri = uris.get(position);
            Bitmap bitmap = MediaStore
                    .Images
                    .Media
                    .getBitmap(contentResolver, uri);
            holder.imageView.setImageBitmap(bitmap);
            holder.progressBar.setVisibility(View.VISIBLE);
            mStorage.child(mVehicleKey + "/" + uri.getLastPathSegment()).putFile(uri)
                    .addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        holder.progressBar.setProgress((int) progress);
                    }).addOnCompleteListener(task -> {
                holder.progressBar.setVisibility(View.GONE);
                holder.doneButton.setVisibility(View.VISIBLE);
            }).addOnFailureListener(e -> FirebaseCrashlytics.getInstance().recordException(e));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return uris.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ProgressBar progressBar;
        public Button doneButton;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.fieldImage);
            this.progressBar = itemView.findViewById(R.id.fieldImageProgress);
            this.doneButton = itemView.findViewById(R.id.fieldImageDone);
        }
    }
}
