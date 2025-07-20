package com.kyron.automation.backend.service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kyron.automation.backend.service.dto.ReviewDto;
import com.kyron.automation.backend.service.dto.ReviewSummaryDto;
import com.kyron.automation.backend.service.model.Product;
import com.kyron.automation.backend.service.model.Review;
import com.kyron.automation.backend.service.repository.ProductRepository;
import com.kyron.automation.backend.service.repository.ReviewRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewService(ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public ReviewDto addReview(Long productId, ReviewDto reviewDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        Review review = new Review();
        BeanUtils.copyProperties(reviewDto, review);
        review.setProduct(product);

        Review savedReview = reviewRepository.save(review);
        ReviewDto savedDto = new ReviewDto();
        BeanUtils.copyProperties(savedReview, savedDto);
        return savedDto;
    }

    public List<ReviewDto> getProductReviews(Long productId, int page, int size) {
        return reviewRepository.findByProductId(productId).stream()
                .map(review -> {
                    ReviewDto dto = new ReviewDto();
                    BeanUtils.copyProperties(review, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public ReviewSummaryDto getReviewSummary(Long productId) {
        Double avgRating = reviewRepository.getAverageRatingForProduct(productId);
        Integer totalReviews = reviewRepository.getReviewCountForProduct(productId);
        
        return new ReviewSummaryDto(
            avgRating != null ? avgRating : 0.0,
            totalReviews != null ? totalReviews : 0
        );
    }
}
