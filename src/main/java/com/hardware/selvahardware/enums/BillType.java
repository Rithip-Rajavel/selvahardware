package com.hardware.selvahardware.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum BillType {
    rough("rough"), 
    final_bill("final");

    private final String value;

    BillType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BillType fromValue(String value) {
        for (BillType billType : BillType.values()) {
            if (billType.value.equals(value) || billType.name().equals(value)) {
                return billType;
            }
        }
        throw new IllegalArgumentException("Unknown bill type: " + value);
    }
}
