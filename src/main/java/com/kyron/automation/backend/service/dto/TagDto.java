package com.kyron.automation.backend.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TagDto {
    private Long id;
    
    @NotBlank(message = "Tag name is required")
    @Size(min = 2, max = 50, message = "Tag name must be between 2 and 50 characters")
    private String name;
}
