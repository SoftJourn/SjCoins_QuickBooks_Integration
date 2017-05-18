package com.softjourn.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Data
public class TransactionDTO {
    private Long id;
    private final String account;
    private final String destination;
    private final BigDecimal amount;
    private final String comment;
    private final Date created;
    private final String status;
    private final String type;
}
