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
    public static ProductUnits fromValue(String string) {
        return ProductUnits.valueOf(string.toLowerCase());

    }

    @JsonValue
    public String getValue() {
        return name();
    }
}
