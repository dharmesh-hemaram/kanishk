package com.dhruv.techapps.adapter;

import android.content.ClipData;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.dhruv.techapps.R;

import java.io.IOException;

public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ViewHolder> {
    public ClipData clipData;
    public Uri uri;
    ContentResolver contentResolver;

    public ImageViewAdapter(Uri uri, ContentResolver contentResolver) {
        this.uri = uri;
        this.contentResolver = contentResolver;
    }

    public ImageViewAdapter(ClipData clipData, ContentResolver contentResolver) {
        this.clipData = clipData;
        this.contentResolver = contentResolver;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.item_image, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            Bitmap bitmap = MediaStore
                    .Images
                    .Media
                    .getBitmap(contentResolver, uri != null ? uri : clipData.getItemAt(position).getUri());
            holder.imageView.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return uri != null ? 1 : clipData.getItemCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.fieldImage);
        }
    }
}
