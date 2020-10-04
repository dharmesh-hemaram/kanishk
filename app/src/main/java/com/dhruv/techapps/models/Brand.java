package com.dhruv.techapps.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Brand {
    @SerializedName("name")
    public String name;
    @SerializedName("models")
    public List<Model> models;
}
