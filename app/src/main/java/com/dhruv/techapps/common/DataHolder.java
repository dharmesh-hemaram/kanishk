package com.dhruv.techapps.common;

import com.dhruv.techapps.models.Brand;
import com.dhruv.techapps.models.Model;

import java.util.List;

public class DataHolder {

    private static final DataHolder holder = new DataHolder();
    private List<Brand> carBrands;
    private List<Brand> tractorBrands;
    private List<Brand> truckBrands;
    private List<Brand> tempoBrands;
    private List<Brand> bikeBrands;

    public static DataHolder getInstance() {
        return holder;
    }

    public List<Brand> getCarBrands() {
        return this.carBrands;
    }

    public void setCarBrands(List<Brand> carBrands) {
        this.carBrands = carBrands;
    }

    public List<Brand> getTractorBrands() {
        return this.tractorBrands;
    }

    public void setTractorBrands(List<Brand> tractorBrands) {
        this.tractorBrands = tractorBrands;
    }

    public List<Brand> getTruckBrands() {
        return this.truckBrands;
    }

    public void setTruckBrands(List<Brand> truckBrands) {
        this.truckBrands = truckBrands;
    }

    public List<Brand> getTempoBrands() {
        return this.tempoBrands;
    }

    public void setTempoBrands(List<Brand> tempoBrands) {
        this.tempoBrands = tempoBrands;
    }

    public List<Brand> getBikeBrands() {
        return this.bikeBrands;
    }

    public void setBikeBrands(List<Brand> bikeBrands) {
        this.bikeBrands = bikeBrands;
    }

    public String[] getBrands(List<Brand> brands) {
        if (brands == null) {
            return new String[]{};
        }
        String[] arr = new String[brands.size()];
        // ArrayList to Array Conversion
        for (int i = 0; i < brands.size(); i++)
            arr[i] = brands.get(i).name;
        return arr;
    }


    public String[] getModels(List<Brand> brands, int brandId) {
        if (brands == null || brands.get(brandId).models == null) {
            return new String[]{"~~TYPE MODEL~~"};
        }
        List<Model> models = brands.get(brandId).models;
        String[] arr = new String[models.size() + 1];
        // ArrayList to Array Conversion
        for (int i = 0; i < models.size(); i++)
            arr[i] = models.get(i).name;
        arr[arr.length - 1] = "~~TYPE MODEL~~";
        return arr;
    }

    public String[] getVariants(List<Brand> brands, int brandId, int modelId) {
        if (brands == null || brands.get(brandId).models == null || brands.get(brandId).models.get(modelId) == null || brands.get(brandId).models.get(modelId).variants == null) {
            return new String[]{"~~TYPE VARIANT~~"};
        }
        String[] variants = brands.get(brandId).models.get(modelId).variants;
        String[] arr = new String[variants.length + 1];
        for (int i = 0; i < variants.length; i++)
            arr[i] = variants[i];
        arr[arr.length - 1] = "~~TYPE VARIANT~~";
        return arr;
    }
}
