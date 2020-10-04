package com.dhruv.techapps.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Model {
    @SerializedName("name")
    public String name;
    @SerializedName("variants")
    public String[] variants;
}
