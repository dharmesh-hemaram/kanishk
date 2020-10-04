package com.dhruv.techapps.models;

import com.google.gson.annotations.SerializedName;

public class Model {
    @SerializedName("name")
    public String name;
    @SerializedName("variants")
    public String[] variants;
}
