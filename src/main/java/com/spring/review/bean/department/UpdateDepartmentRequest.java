package com.spring.review.bean.department;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateDepartmentRequest {

    @NotBlank(message = "Name wajib diisi")
    private String name;

    private String description;

    private Boolean isActive;

}
