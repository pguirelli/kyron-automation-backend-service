package com.kyron.automation.backend.service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean active;

    private CategoryDto category;
    private Set<TagDto> tags;
    private List<ReviewDto> reviews;
    private List<String> imageUrls;
    private Map<String, String> specifications;

    // Campos calculados
    private Double averageRating;
    private Integer totalReviews;
}
