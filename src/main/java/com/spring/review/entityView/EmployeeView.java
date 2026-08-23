package com.spring.review.entityView;

import com.blazebit.persistence.view.EntityView;
import com.blazebit.persistence.view.Mapping;
import com.spring.review.entity.EmployeeEntity;
import com.spring.review.entity.EmployeeStatus;
import com.spring.review.entity.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EntityView(EmployeeEntity.class)
public interface EmployeeView {

    Long getId();

    String getEmployeeCode();

    String getFullName();

    String getEmail();

    String getPhoneNumber();

    Gender getGender();

    LocalDate getBirthDate();

    LocalDate getHireDate();

    EmployeeStatus getStatus();

    @Mapping("department.name")
    String getDepartmentName();

    @Mapping("position.name")
    String getPositionName();

    @Mapping("manager.id")
    Long getManagerId();

    @Mapping("manager.fullName")
    String getManagerName();

    String getPhotoUrl();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();


}
