package com.dhruv.techapps.common;

import com.dhruv.techapps.models.Brand;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Common {

    public final static String[] TYPES = {"Cars", "Tractors", "Trucks", "Tempos", "Bikes"};
    public final static String[] VEHICLE_STATUS = {"Running", "Break Down"};
    public final static String[] ENGINE_TYPES = {"Petrol", "Diesel", "CNG"};
    public static final int REQUEST_CODE_PROFILE = 1;
    public static final String EXTRA_VERIFICATION_ID = "verificationId";
    public static final String EXTRA_PHONE_NUMBER = "phoneNumber";
    public static final String EXTRA_TOKEN = "token";
    public static final String EXTRA_DISPLAY_NAME = "displayName";
    public static final String EXTRA_IMAGE_URI = "imageUri";
    public static final String EXTRA_VEHICLE_TYPE = "vehicleType";
    public static final String EXTRA_VEHICLE_KEY = "vehicleKey";
    public static final String EXTRA_VEHICLE_PRICE = "vehiclePrice";
    public static final String EXTRA_VEHICLE_NAME = "vehicleName";
    private static final String TAG = "Common";

    public static String formatCurrency(String currency) {
        double parsed = Double.parseDouble(currency + "00");
        return NumberFormat.getCurrencyInstance(new Locale("en", "in")).format((parsed / 100));
    }

    public static String formatCurrency(double parsed) {
        return NumberFormat.getCurrencyInstance(new Locale("en", "in")).format(parsed);
    }

    public static String removeCurrencyFormatter(String currency) throws ParseException {
        return Objects.requireNonNull(NumberFormat.getCurrencyInstance(new Locale("en", "in")).parse(currency)).toString();
    }

    public static String formatDecimal(String number) {
        double parsed = Double.parseDouble(number + "00");
        return NumberFormat.getInstance(new Locale("en", "in")).format((parsed / 100));
    }

    public static String formatDecimal(double parsed) {
        if (parsed > 99999d) {
            return ((int) parsed) / 100000 + " lakh";
        }
        return NumberFormat.getInstance(new Locale("en", "in")).format(parsed);
    }

    public static String removeDecimalFormatter(String number) throws ParseException {
        return Objects.requireNonNull(NumberFormat.getInstance(new Locale("en", "in")).parse(number)).toString();
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
        Car(0), Tractor(1), Truck(2), Tempo(3), Bike(4);


        private final int code;

        ENUM_TYPES(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }


}
