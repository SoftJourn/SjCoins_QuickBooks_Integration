package com.softjourn.dto;

import lombok.Data;

import java.util.List;

@Data
public class Page<T> {
    private final int number;
    private final int size;
    private final int numberOfElements;
    private final int totalPages;
    private final int totalElements;
    private final boolean first;
    private final boolean last;
    private final List<T> content;
}
