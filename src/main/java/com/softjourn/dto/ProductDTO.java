package com.softjourn.dto;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class ProductDTO {
    private Long id;
    private final String name;
    private final String description;
    private final BigDecimal price;
}
