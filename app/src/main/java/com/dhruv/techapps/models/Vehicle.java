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
    public String reg;
    public String color;
    public String mobile;
    public String ins;
    public String loc;

    public int eType;
    public int year;
    public int km;
    public int status;

    public boolean rc;
    public boolean form36;
    public boolean form35;
    public boolean sold;

    public double price;
    public double bid;

    public Vehicle() {
    }

    public Vehicle(String uid, String name, String reg, String color, String mobile, String ins, String loc, int eType, int year, String km, int status, boolean rc, boolean form35, boolean form36, String price) throws ParseException {
        this.uid = uid;
        this.name = name;
        this.reg = reg;
        this.color = color;
        this.mobile = mobile;
        this.ins = ins;
        this.loc = loc;
        this.eType = eType;
        this.year = year;
        if (!TextUtils.isEmpty(km)) {
            this.km = Integer.parseInt(Common.removeDecimalFormatter(km));
        }
        this.status = status;
        this.rc = rc;
        this.form35 = form35;
        this.form36 = form36;
        this.price = Double.parseDouble(Common.removeCurrencyFormatter(price));
    }

    @Exclude
    public String getEngineTypeName() {
        return Common.ENGINE_TYPES[eType];
    }


    @Exclude
    public String getStatusName() {
        return Common.VEHICLE_STATUS[status];
    }

    @Exclude
    public String getRC() {
        return rc ? "Yes" : "No";
    }

    @Exclude
    public String getForm35() {
        return form35 ? "Yes" : "No";
    }

    @Exclude
    public String getForm36() {
        return form36 ? "Yes" : "No";
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("name", name);
        result.put("reg", reg);
        if (!TextUtils.isEmpty(color)) {
            result.put("color", color);
        }
        if (!TextUtils.isEmpty(mobile)) {
            result.put("mobile", mobile);
        }
        if (!TextUtils.isEmpty(ins)) {
            result.put("ins", ins);
        }
        if (!TextUtils.isEmpty(loc)) {
            result.put("loc", loc);
        }

        result.put("eType", eType);
        result.put("year", year);
        result.put("km", km);
        result.put("status", status);

        result.put("rc", rc);
        result.put("form35", form35);
        result.put("form36", form36);
        result.put("sold", sold);

        result.put("price", price);
        result.put("bid", bid);
        return result;
    }
    // [END post_to_map]
}
// [END post_class]
