package com.spring.review.bean.position;

import com.spring.review.common.PageSpec;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PositionSearchRequest extends PageSpec {

    private String name;

    private String positionCode;

    private Long departmentId;

    private Boolean isActive;

}
