package com.dhruv.techapps.services;

import com.dhruv.techapps.models.Brand;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GitHubService {

    @GET("/dharmesh-hemaram/kanishk/main/car-brands.json")
    Call<List<Brand>> getCardBrands();

    @GET("/dharmesh-hemaram/kanishk/main/tractor-brands.json")
    Call<List<Brand>> getTractorBrands();

    @GET("/dharmesh-hemaram/kanishk/main/truck-brands.json")
    Call<List<Brand>> getTruckBrands();

    @GET("/dharmesh-hemaram/kanishk/main/tempo-brands.json")
    Call<List<Brand>> getTempoBrands();

    @GET("/dharmesh-hemaram/kanishk/main/bike-brands.json")
    Call<List<Brand>> getBikeBrands();
}
