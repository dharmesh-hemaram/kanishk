package com.dhruv.techapps.ui.home;

import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dhruv.techapps.R;
import com.dhruv.techapps.common.Common;
import com.dhruv.techapps.models.Vehicle;

public class HomeViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "HomeViewHolder";
    public TextView nameView;
    public TextView priceView;
    public TextView kmView;
    public TextView yearView;
    public ImageView imageView;
    public TextView regView;

    public HomeViewHolder(View itemView) {
        super(itemView);

        nameView = itemView.findViewById(R.id.carName);
        priceView = itemView.findViewById(R.id.carPrice);
        kmView = itemView.findViewById(R.id.carKM);
        yearView = itemView.findViewById(R.id.carYear);
        regView = itemView.findViewById(R.id.carRegistrationNumber);
        imageView = itemView.findViewById(R.id.carImage);
    }

    public void bindToPost(Resources resources, Vehicle vehicle) {

        nameView.setText(vehicle.name);
        regView.setText(vehicle.reg.toUpperCase());
        if (vehicle.bid <= 0) {
            priceView.setText(Common.formatCurrency(vehicle.price));
        } else {
            priceView.setText(Common.formatCurrency(vehicle.bid));
        }
        kmView.setText(resources.getString(R.string.km_string, Common.formatDecimal(vehicle.km)));
        yearView.setText(resources.getString(R.string.year_string, Integer.toString(vehicle.year)));
    }
}
