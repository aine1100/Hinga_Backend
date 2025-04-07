package com.Hinga.farmMis.Constants;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LivestockStatus {
    HEALTHY,
    CRITICAL,
    NEEDS_ATTENTION;

    @JsonCreator
    public static LivestockStatus fromValue(String value) {
        return valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
