package com.dhruv.techapps.models;

import android.text.TextUtils;

import com.dhruv.techapps.common.Common;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.ParseException;
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
    public boolean rc;
    public String location;
    public int status;
    public boolean form36;
    public boolean form28;
    public boolean soldOut;

    public Vehicle() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Vehicle(String uid, String name, int eType, String year, String price, String regNum, String km, String color, String mobile, String ins) throws ParseException {
        this.uid = uid;
        this.name = name;
        this.eType = eType;
        this.year = Integer.parseInt(year);
        this.price = Double.parseDouble(Common.removeCurrencyFormatter(price));
        this.reg = regNum;
        if (km != null && !km.isEmpty()) {
            this.km = Integer.parseInt(Common.removeDecimalFormatter(km));
        }
        this.color = color;
        this.mobile = mobile;
        this.ins = ins;
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
        result.put("eType", eType);
        result.put("rc", rc);
        if (!TextUtils.isEmpty(color)) {
            result.put("color", color);
        }
        if (!TextUtils.isEmpty(mobile)) {
            result.put("mobile", mobile);
        }
        if (!TextUtils.isEmpty(ins)) {
            result.put("ins", ins);
        }
        if (!TextUtils.isEmpty(location)) {
            result.put("location", location);
        }
        result.put("status", status);
        result.put("form28", form28);
        result.put("form36", form36);
        result.put("soldOut", soldOut);
        return result;
    }
    // [END post_to_map]
}
// [END post_class]
