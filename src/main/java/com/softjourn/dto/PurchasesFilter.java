package com.softjourn.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
public class PurchasesFilter {
    private final int machineId;
    private final String type;

    @JsonProperty("timeZoneOffSet")
    private final int timeZoneOffset;
    private final String start;
    private final String due;
}
