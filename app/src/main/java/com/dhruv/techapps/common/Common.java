package com.dhruv.techapps.common;

import android.util.Log;

import com.dhruv.techapps.models.Brand;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;

public class Common {

    public final static String[] TYPES = {"Cars", "Tractors", "Trucks", "Tempos", "Bikes"};
    public final static String[] ENGINE_TYPES = {"Petrol", "Diesel", "CNG"};
    private static final String TAG = "Common";

    public final static String formatCurrency(String currency) {
        Log.d(TAG, currency);
        double parsed = Double.parseDouble(currency + "00");
        Log.d(TAG, currency + "00");
        return NumberFormat.getCurrencyInstance(new Locale("en", "in")).format((parsed / 100));
    }

    public final static String removeCurrencyFormatter(String currency) throws ParseException {
        Log.d(TAG, currency);
        return NumberFormat.getCurrencyInstance(new Locale("en", "in")).parse(currency).toString();
    }

    public final static String formatDecimal(String number) {
        Log.d(TAG, number);
        double parsed = Double.parseDouble(number + "00");
        Log.d(TAG, number);
        return NumberFormat.getInstance(new Locale("en", "in")).format((parsed / 100));
    }

    public final static String removeDecimalFormatter(String currency) throws ParseException {
        Log.d(TAG, currency);
        return NumberFormat.getInstance(new Locale("en", "in")).parse(currency).toString();
    }

    public static List<Brand> getBrands(int typeId, DataHolder dataHolder) {
        List<Brand> brands;
        switch (typeId) {
            case 0:
                brands = dataHolder.getCarBrands();
                break;
            case 1:
                brands = dataHolder.getTractorBrands();
                break;
            case 2:
                brands = dataHolder.getTruckBrands();
                break;
            case 3:
                brands = dataHolder.getTempoBrands();
                break;
            case 4:
                brands = dataHolder.getBikeBrands();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + typeId);
        }
        return brands;
    }

    public enum ENUM_TYPES {
        Car(0), Truck(1), Tempo(2), Bike(3);


        private final int code;

        ENUM_TYPES(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }
}
