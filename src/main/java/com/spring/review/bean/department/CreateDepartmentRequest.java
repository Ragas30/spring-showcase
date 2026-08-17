package com.spring.review.bean.department;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDepartmentRequest {

    @NotBlank(message = "Name wajib diisi")
    private String name;

    private String description;

}
