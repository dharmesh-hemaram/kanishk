package com.dhruv.techapps.viewholder;

import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dhruv.techapps.R;
import com.dhruv.techapps.common.Common;
import com.dhruv.techapps.models.Vehicle;

public class VehicleViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "VehicleViewHolder";
    public TextView nameView;
    public TextView priceView;
    public TextView kmView;
    public TextView yearView;
    public ImageView imageView;
    public TextView regView;

    public VehicleViewHolder(View itemView) {
        super(itemView);

        nameView = itemView.findViewById(R.id.carName);
        priceView = itemView.findViewById(R.id.carPrice);
        kmView = itemView.findViewById(R.id.carKM);
        yearView = itemView.findViewById(R.id.carYear);
        regView = itemView.findViewById(R.id.carRegistrationNumber);
        imageView = itemView.findViewById(R.id.carImage);
    }

    public void bindToPost(Resources resources, Vehicle vehicle, View.OnClickListener starClickListener) {

        nameView.setText(vehicle.name);
        regView.setText(vehicle.reg.toUpperCase());
        priceView.setText(Common.formatCurrency(vehicle.price));
        Log.d(TAG, vehicle.km + "~~" + Common.formatDecimal(vehicle.km));

        kmView.setText(resources.getString(R.string.km_string, Common.formatDecimal(vehicle.km)));
        yearView.setText(resources.getString(R.string.year_string, Integer.toString(vehicle.year)));
    }
}
