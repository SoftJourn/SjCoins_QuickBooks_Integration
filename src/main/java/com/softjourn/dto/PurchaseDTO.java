package com.softjourn.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Data
public class PurchaseDTO {
    private final String account;
    private final Date date;
    private final String product;
    private final BigDecimal price;
}
