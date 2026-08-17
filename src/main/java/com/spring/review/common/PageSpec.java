package com.spring.review.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageSpec {

    @Min(0)
    private int page = 0;

    @Min(1)
    @Max(100)
    private int size = 10;
}
