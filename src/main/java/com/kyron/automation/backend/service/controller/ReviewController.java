package com.kyron.automation.backend.service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kyron.automation.backend.service.dto.ReviewDto;
import com.kyron.automation.backend.service.dto.ReviewSummaryDto;
import com.kyron.automation.backend.service.service.ReviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewDto> addReview(
            @PathVariable Long productId,
            @Valid @RequestBody ReviewDto reviewDto) {
        return ResponseEntity.ok(reviewService.addReview(productId, reviewDto));
    }

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.getProductReviews(productId, page, size));
    }

    @GetMapping("/summary")
    public ResponseEntity<ReviewSummaryDto> getReviewSummary(
            @PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getReviewSummary(productId));
    }
}
