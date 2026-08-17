package com.spring.review.bean.position;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePositionRequest {

    @NotBlank(message = "Name wajib diisi")
    private String name;

    private String description;

    @NotNull(message = "Department wajib diisi")
    private Long departmentId;

}
