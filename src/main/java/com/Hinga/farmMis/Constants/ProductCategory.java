package com.Hinga.farmMis.Constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductCategory {
    CROPS,
    VEGETABLES,
    FRUITS,
    OTHERS,
    ANIMAL_PRODUCTS,
    DIARY;

    @JsonCreator
    public static ProductCategory fromValue(String value) {
        return ProductCategory.valueOf(value.toUpperCase());
    }
    @JsonValue
    public String toValue() {
        return name();
    }


}
