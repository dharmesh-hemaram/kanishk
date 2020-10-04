package com.dhruv.techapps.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.dhruv.techapps.DataHolder;
import com.dhruv.techapps.R;
import com.dhruv.techapps.models.Car;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CarViewHolder extends RecyclerView.ViewHolder {

    public TextView brandView;
    public TextView modalView;
    public TextView variantView;
    public TextView priceView;
    public TextView kmView;
    public TextView yearView;
    public ImageView imageView;

    public CarViewHolder(View itemView) {
        super(itemView);

        brandView = itemView.findViewById(R.id.carBrand);
        modalView = itemView.findViewById(R.id.carModel);
        variantView = itemView.findViewById(R.id.carVariant);
        priceView = itemView.findViewById(R.id.carPrice);
        kmView = itemView.findViewById(R.id.carKM);
        yearView = itemView.findViewById(R.id.carYear);
        imageView = itemView.findViewById(R.id.carImage);
    }

    public void bindToPost(Car car, View.OnClickListener starClickListener) {


        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');

        DecimalFormat currencyFormat = new DecimalFormat("₹ #,###", symbols);
        DecimalFormat decimalFormat = new DecimalFormat("#,###", symbols);

        String[] name = car.brand.split(",");
        String brand = DataHolder.getInstance().getBrands()[Integer.parseInt(name[0])];
        String model = DataHolder.getInstance().getModels(Integer.parseInt(name[0]))[Integer.parseInt(name[1])];
        String variant = DataHolder.getInstance().getVariants(Integer.parseInt(name[0]),Integer.parseInt(name[1]))[Integer.parseInt(name[2])];

        brandView.setText(brand);
        modalView.setText(model);
        variantView.setText(variant);
        priceView.setText(currencyFormat.format(car.price));
        kmView.setText("Km: " + decimalFormat.format(car.km));
        yearView.setText("Year: " + String.valueOf(car.year));
    }
}
