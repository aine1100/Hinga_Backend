package com.Hinga.farmMis.Constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductUnits {
    KILOGRAM,
    GRAM,
    POUND,
    UNIT,
    DOZEN,
    LITER;

    @JsonCreator
    public static ProductUnits fromValue(String value) {
        try {
            return ProductUnits.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @JsonValue
    public String getValue() {
        return name();
    }
}
