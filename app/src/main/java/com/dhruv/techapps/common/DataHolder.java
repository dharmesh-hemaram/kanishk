package com.dhruv.techapps.common;

import com.dhruv.techapps.models.Brand;
import com.dhruv.techapps.models.Model;

import java.util.List;

public class DataHolder {

    private static final DataHolder holder = new DataHolder();
    private List<Brand> brands;

    public static DataHolder getInstance() {
        return holder;
    }

    public String[] getBrands() {
        if (brands == null) {
            return new String[]{};
        }
        String[] arr = new String[brands.size()];
        // ArrayList to Array Conversion
        for (int i = 0; i < brands.size(); i++)
            arr[i] = brands.get(i).name;
        return arr;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }

    public String[] getModels(int brandId) {
        if (brands == null || brands.get(brandId).models == null) {
            return new String[]{"~~Models not found~~"};
        }
        List<Model> models = brands.get(brandId).models;
        String[] arr = new String[models.size()];
        // ArrayList to Array Conversion
        for (int i = 0; i < models.size(); i++)
            arr[i] = models.get(i).name;
        return arr;
    }

    public String[] getVariants(int brandId, int modelId) {
        if (brands == null || brands.get(brandId).models == null || brands.get(brandId).models.get(modelId).variants == null) {
            return new String[]{"~~Variants not found~~"};
        }
        return brands.get(brandId).models.get(modelId).variants;
    }
}
