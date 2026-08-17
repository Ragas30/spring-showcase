package com.spring.review.common;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PageResponse<T> {

    private List<T> content;

    private long totalElements;

    private int page;

    private int size;

    private int totalPages;
}
