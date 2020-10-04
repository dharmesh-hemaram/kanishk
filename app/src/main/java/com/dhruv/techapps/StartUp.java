package com.dhruv.techapps;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.dhruv.techapps.common.DataHolder;
import com.dhruv.techapps.models.Brand;
import com.dhruv.techapps.services.APIClient;
import com.dhruv.techapps.services.GitHubService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartUp extends Application {

    private static final String TAG = "StartUp";

    @Override
    public void onCreate() {
        super.onCreate();

        Call<List<Brand>> call = APIClient.getClient().create(GitHubService.class).getBrands();
        call.enqueue(new Callback<List<Brand>>() {
            @Override
            public void onResponse(Call<List<Brand>> brands, Response<List<Brand>> response) {
                Log.d(TAG, response.body().toString());
                DataHolder.getInstance().setBrands(response.body());
            }

            @Override
            public void onFailure(Call<List<Brand>> call, Throwable t) {
                call.cancel();
                Log.e(TAG, t.getMessage());
                Toast.makeText(getApplicationContext(), "Brands Loading Failed", Toast.LENGTH_SHORT).show();
            }
        });
// Place your code here which will be executed only once
    }
}