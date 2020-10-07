package com.dhruv.techapps.common;

import com.dhruv.techapps.models.Brand;

import java.util.List;

public class Common {
    public final static String[] TYPES = {"Car", "Tractor", "Truck", "Tempo", "Bike"};
    public final static String[] ENGINE_TYPES = {"Petrol", "Diesel", "CNG"};

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
