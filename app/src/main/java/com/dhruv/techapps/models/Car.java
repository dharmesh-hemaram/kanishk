package com.dhruv.techapps.models;

import com.dhruv.techapps.Common;
import com.dhruv.techapps.DataHolder;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Car {

    public String uid;
    public String brand;
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

    public Car(String uid, String brand, String year, String price, String regNum, String km, String owners, String color, Integer type, String mobile, String ins) {
        this.uid = uid;
        this.brand = brand;
        this.year = Integer.parseInt(year);
        this.price = Double.parseDouble(price);
        this.reg = regNum;
        this.km = Integer.parseInt(km);
        this.owners = Integer.parseInt(owners);
        this.color = color;
        this.type = type;
        this.mobile = mobile;
        this.ins = ins;
        this.updated = new HashMap<String, Object>();
        this.updated.put("date", ServerValue.TIMESTAMP);
    }

    @Exclude
    public long getUpdatedOnLong() {
        return (long) updated.get("date");
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
