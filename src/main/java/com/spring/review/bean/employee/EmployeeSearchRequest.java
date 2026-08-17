package com.spring.review.bean.employee;

import com.spring.review.common.PageSpec;
import com.spring.review.entity.EmployeeStatus;
import com.spring.review.entity.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeSearchRequest extends PageSpec {

    private String fullName;

    private String employeeCode;

    private String email;

    private Gender gender;

    private EmployeeStatus status;

    private Long departmentId;

    private Long positionId;

}
