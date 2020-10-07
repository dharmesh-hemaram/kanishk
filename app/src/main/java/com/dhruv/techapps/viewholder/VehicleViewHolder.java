package com.dhruv.techapps.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dhruv.techapps.R;
import com.dhruv.techapps.models.Vehicle;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class VehicleViewHolder extends RecyclerView.ViewHolder {

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

    public void bindToPost(Vehicle vehicle, View.OnClickListener starClickListener) {


        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        DecimalFormat currencyFormat = new DecimalFormat("₹ #,###", symbols);
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);

        nameView.setText(vehicle.name);
        regView.setText(vehicle.reg.toUpperCase());
        priceView.setText(currencyFormat.format(vehicle.price));
        kmView.setText("Km: " + decimalFormat.format(vehicle.km));
        yearView.setText("Year: " + String.valueOf(vehicle.year));
    }
}
