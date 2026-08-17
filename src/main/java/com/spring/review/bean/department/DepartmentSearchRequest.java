package com.spring.review.bean.department;

import com.spring.review.common.PageSpec;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentSearchRequest extends PageSpec {

    private String name;

    private String departmentCode;

    private Boolean isActive;

}
