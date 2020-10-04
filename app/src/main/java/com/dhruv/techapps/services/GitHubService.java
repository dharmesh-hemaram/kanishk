package com.dhruv.techapps.services;

import com.dhruv.techapps.models.Brand;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GitHubService {

    @GET("/Dhruv-Techapps/car-bid/master/brands.json")
    Call<List<Brand>> getBrands();
}
