package com.dhruv.techapps.models;

import com.dhruv.techapps.common.Common;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Vehicle {

    public String uid;
    public String name;
    public int eType;
    public int year;
    public double price;
    public String reg;
    public int km;
    public String color;
    public String mobile;
    public String ins;
    public Object updated;
    public boolean soldout;

    public Vehicle() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Vehicle(String uid, String name, int eType, String year, String price, String regNum, String km, String color, String mobile, String ins) {
        this.uid = uid;
        this.name = name;
        this.eType = eType;
        this.year = Integer.parseInt(year);
        this.price = Double.parseDouble(price);
        this.reg = regNum;
        if (km != null && !km.isEmpty()) {
            this.km = Integer.parseInt(km);
        }
        this.color = color;
        this.mobile = mobile;
        this.ins = ins;
        this.updated = ServerValue.TIMESTAMP;
    }

    @Exclude
    public long getUpdatedOnLong() {
        return (long) updated;
    }

    @Exclude
    public String getEngineTypeName() {
        return Common.ENGINE_TYPES[eType];
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        result.put("year", year);
        result.put("price", price);
        result.put("reg", reg);
        result.put("km", km);
        if (color != null && !color.isEmpty()) {
            result.put("color", color);
        }
        if (mobile != null && !mobile.isEmpty()) {
            result.put("mobile", mobile);
        }
        if (ins != null && !ins.isEmpty()) {
            result.put("ins", ins);
        }
        result.put("eType", eType);
        result.put("updated", updated);
        return result;
    }
    // [END post_to_map]
}
// [END post_class]
