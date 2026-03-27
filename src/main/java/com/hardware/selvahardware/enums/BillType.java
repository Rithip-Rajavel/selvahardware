package com.hardware.selvahardware.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum BillType {
    rough, 
    @JsonProperty("final")
    final_bill;
}
