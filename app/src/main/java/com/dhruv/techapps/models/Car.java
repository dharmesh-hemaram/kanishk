package com.dhruv.techapps.models;

import com.dhruv.techapps.Common;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Car {

    public String uid;
    public int brand;
    public int model;
    public int variant;
    public int year;
    public double price;
    public String reg;
    public int km;
    public int owners;
    public String color;
    public int type;
    public String mobile;
    public String ins;
    public HashMap<String, Object> updated;

    public Car() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Car(String uid, Integer brand, Integer model, Integer variant, String year, String price, String regNum, String km, String owners, String color, Integer type, String mobile, String insurance) {
        this.uid = uid;
        this.brand = brand;
        this.model = model;
        this.variant = variant;
        this.year = Integer.parseInt(year);
        this.price = Double.parseDouble(price);
        this.reg = regNum;
        this.km = Integer.parseInt(km);
        this.owners = Integer.parseInt(owners);
        this.color = color;
        this.type =  type;
        this.mobile = mobile;
        this.ins = ins;
        this.updated = new HashMap<String, Object>();
        this.updated.put("date",ServerValue.TIMESTAMP);
    }

    @Exclude
    public long getUpdatedOnLong() {
        return (long) updated.get("date");
    }

    @Exclude
    public String getBrandName() {
        return Common.BRANDS[brand];
    }

    @Exclude
    public String getModelName() {
        return Common.MODELS[model];
    }

    @Exclude
    public String getVariantName() {
        return Common.VARIANTS[variant];
    }

    @Exclude
    public String getTypeName() {
        return Common.TYPES[type];
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("brand", brand);
        result.put("model", model);
        result.put("variant", variant);
        result.put("year", year);
        result.put("price", price);
        result.put("reg", reg);
        result.put("km", km);
        result.put("owners", owners);
        result.put("color", color);
        result.put("type", type);
        result.put("mobile", mobile);
        result.put("ins", ins);
        result.put("updated", updated);

        return result;
    }
    // [END post_to_map]
}
// [END post_class]
