package com.dhruv.techapps;

import android.app.Application;
import android.widget.Toast;

import com.dhruv.techapps.common.DataHolder;
import com.dhruv.techapps.models.Brand;
import com.dhruv.techapps.services.APIClient;
import com.dhruv.techapps.services.GitHubService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

@EverythingIsNonNull
public class StartUp extends Application {

    private static final String TAG = "StartUp";

    @Override
    public void onCreate() {
        super.onCreate();
        this.setCarBrands();
        this.setTractorBrands();
        this.setTruckBrands();
        this.setTempoBrands();
        this.setBikeBrands();
    }

    private void setCarBrands() {
        Call<List<Brand>> call = APIClient.getClient().create(GitHubService.class).getCardBrands();
        call.enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> brands, Response<List<Brand>> response) {
                DataHolder.getInstance().setCarBrands(response.body());
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Car Brands Loading Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setTractorBrands() {
        Call<List<Brand>> call = APIClient.getClient().create(GitHubService.class).getTractorBrands();
        call.enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> brands, Response<List<Brand>> response) {
                DataHolder.getInstance().setTractorBrands(response.body());
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Car Brands Loading Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setTruckBrands() {
        Call<List<Brand>> call = APIClient.getClient().create(GitHubService.class).getTruckBrands();
        call.enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> brands, Response<List<Brand>> response) {
                DataHolder.getInstance().setTruckBrands(response.body());
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Truck Brands Loading Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setTempoBrands() {
        Call<List<Brand>> call = APIClient.getClient().create(GitHubService.class).getTempoBrands();
        call.enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> brands, Response<List<Brand>> response) {
                DataHolder.getInstance().setTempoBrands(response.body());
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Tempo Brands Loading Failed", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setBikeBrands() {
        Call<List<Brand>> call = APIClient.getClient().create(GitHubService.class).getBikeBrands();
        call.enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> brands, Response<List<Brand>> response) {
                DataHolder.getInstance().setBikeBrands(response.body());
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {
                call.cancel();
                Toast.makeText(getApplicationContext(), "Bike Brands Loading Failed", Toast.LENGTH_LONG).show();
            }
        });
    }
}