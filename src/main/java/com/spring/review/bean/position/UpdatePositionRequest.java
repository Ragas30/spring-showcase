package com.spring.review.bean.position;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePositionRequest {

    @NotBlank(message = "Name wajib diisi")
    private String name;

    private String description;

    private Long departmentId;

    private Boolean isActive;

}
