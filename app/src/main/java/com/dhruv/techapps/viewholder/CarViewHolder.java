package com.dhruv.techapps.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhruv.techapps.R;
import com.dhruv.techapps.models.Car;

public class CarViewHolder extends RecyclerView.ViewHolder {

    public TextView brandView;
    public TextView modalView;
    public TextView variantView;
    public TextView priceView;
    public TextView kmView;
    public TextView yearView;

    public CarViewHolder(View itemView) {
        super(itemView);

        brandView = itemView.findViewById(R.id.carBrand);
        modalView = itemView.findViewById(R.id.carModel);
        variantView = itemView.findViewById(R.id.carVariant);
        priceView = itemView.findViewById(R.id.carPrice);
        kmView = itemView.findViewById(R.id.carKM);
        yearView= itemView.findViewById(R.id.carYear);
    }

    public void bindToPost(Car car, View.OnClickListener starClickListener) {
        brandView.setText(car.getBrandName());
        modalView.setText(car.getModelName());
        variantView.setText(car.getVariantName());
        priceView.setText(Double.toString(car.price));
        kmView.setText(String.valueOf(car.km));
        yearView.setText(String.valueOf(car.year));
    }
}
