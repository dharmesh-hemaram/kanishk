package com.dhruv.techapps.viewholder;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dhruv.techapps.R;

public class ImageViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    public ImageViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.fieldImage);
    }

    public void bind(Uri uri) {
        imageView.setImageURI(uri);
    }
}
