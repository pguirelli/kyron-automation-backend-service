package com.kyron.automation.backend.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSummaryDto {
    private Double averageRating;
    private Integer totalReviews;
}
