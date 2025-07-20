package com.kyron.automation.backend.service.service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import com.kyron.automation.backend.service.model.Category;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kyron.automation.backend.service.dto.CategoryDto;
import com.kyron.automation.backend.service.dto.ProductRequestDto;
import com.kyron.automation.backend.service.dto.ProductResponseDto;
import com.kyron.automation.backend.service.dto.ReviewDto;
import com.kyron.automation.backend.service.dto.TagDto;
import com.kyron.automation.backend.service.model.Product;
import com.kyron.automation.backend.service.model.Tag;
import com.kyron.automation.backend.service.repository.CategoryRepository;
import com.kyron.automation.backend.service.repository.ProductRepository;
import com.kyron.automation.backend.service.repository.ReviewRepository;
import com.kyron.automation.backend.service.repository.TagRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ReviewRepository reviewRepository;

    public ProductService(ProductRepository productRepository,
                        CategoryRepository categoryRepository,
                        TagRepository tagRepository,
                        ReviewRepository reviewRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Product product = new Product();
        BeanUtils.copyProperties(requestDto, product, "categoryId", "tagIds");

        // Configura a categoria se fornecida
        if (requestDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + requestDto.getCategoryId()));
            product.setCategory(category);
        }

        // Configura as tags se fornecidas
        if (requestDto.getTagIds() != null && !requestDto.getTagIds().isEmpty()) {
            Set<Tag> tags = tagRepository.findByIdIn(requestDto.getTagIds());
            if (tags.size() != requestDto.getTagIds().size()) {
                throw new EntityNotFoundException("One or more tags were not found");
            }
            product.setTags(tags);
        }

        // Inicializa as coleções para evitar NPE
        if (product.getTags() == null) {
            product.setTags(new HashSet<>());
        }
        if (product.getReviews() == null) {
            product.setReviews(new ArrayList<>());
        }
        if (product.getImageUrls() == null) {
            product.setImageUrls(new ArrayList<>());
        }
        if (product.getSpecifications() == null) {
            product.setSpecifications(new HashMap<>());
        }

        Product savedProduct = productRepository.save(product);
        return convertToResponseDto(savedProduct);
    }

    public ProductResponseDto getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return convertToResponseDto(product);
    }

    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        
        BeanUtils.copyProperties(requestDto, product);
        Product updatedProduct = productRepository.save(product);
        return convertToResponseDto(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        product.setActive(false);
        productRepository.save(product);
    }

    @Transactional
    public ProductResponseDto addTags(Long id, Set<Long> tagIds) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        
        // Inicializa a coleção de tags se for null
        if (product.getTags() == null) {
            product.setTags(new HashSet<>());
        }
        
        // Busca todas as tags solicitadas
        Set<Tag> tagsToAdd = tagRepository.findByIdIn(tagIds);
        
        // Verifica se todas as tags foram encontradas
        if (tagsToAdd.size() != tagIds.size()) {
            throw new EntityNotFoundException("One or more tags were not found");
        }
        
        // Adiciona as novas tags
        product.getTags().addAll(tagsToAdd);
        
        // Salva o produto
        Product savedProduct = productRepository.save(product);
        return convertToResponseDto(savedProduct);
    }

    @Transactional
    public ProductResponseDto addSpecifications(Long id, Map<String, String> specifications) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        
        product.getSpecifications().putAll(specifications);
        Product savedProduct = productRepository.save(product);
        return convertToResponseDto(savedProduct);
    }

    @Transactional
    public ProductResponseDto addImages(Long id, List<String> imageUrls) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        
        product.getImageUrls().addAll(imageUrls);
        Product savedProduct = productRepository.save(product);
        return convertToResponseDto(savedProduct);
    }

    public List<ProductResponseDto> getProductsByCategory(Long categoryId) {
        return productRepository.findAll().stream()
                .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(categoryId))
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> getProductsByTag(Long tagId) {
        return productRepository.findAll().stream()
                .filter(p -> p.getTags().stream().anyMatch(t -> t.getId().equals(tagId)))
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> searchProducts(
            String name,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Long categoryId,
            Set<Long> tagIds,
            Double minRating) {
        
        return productRepository.findAll().stream()
                .filter(p -> name == null || p.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(p -> minPrice == null || p.getPrice().compareTo(minPrice) >= 0)
                .filter(p -> maxPrice == null || p.getPrice().compareTo(maxPrice) <= 0)
                .filter(p -> categoryId == null || (p.getCategory() != null && p.getCategory().getId().equals(categoryId)))
                .filter(p -> tagIds == null || !Collections.disjoint(p.getTags().stream().map(Tag::getId).collect(Collectors.toSet()), tagIds))
                .filter(p -> minRating == null || getProductAverageRating(p.getId()) >= minRating)
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private double getProductAverageRating(Long productId) {
        Double avgRating = reviewRepository.getAverageRatingForProduct(productId);
        return avgRating != null ? avgRating : 0.0;
    }

    public List<ProductResponseDto> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDto> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    private ProductResponseDto convertToResponseDto(Product product) {
        ProductResponseDto responseDto = new ProductResponseDto();
        
        // Copiando propriedades básicas
        responseDto.setId(product.getId());
        responseDto.setName(product.getName());
        responseDto.setDescription(product.getDescription());
        responseDto.setPrice(product.getPrice());
        responseDto.setStockQuantity(product.getStockQuantity());
        responseDto.setCreatedAt(product.getCreatedAt());
        responseDto.setUpdatedAt(product.getUpdatedAt());
        responseDto.setActive(product.getActive());
        
        // Configurando categoria
        if (product.getCategory() != null) {
            CategoryDto categoryDto = new CategoryDto();
            BeanUtils.copyProperties(product.getCategory(), categoryDto);
            responseDto.setCategory(categoryDto);
        }
        
        // Configurando tags
        if (product.getTags() != null) {
            Set<TagDto> tagDtos = product.getTags().stream()
                .map(tag -> {
                    TagDto tagDto = new TagDto();
                    BeanUtils.copyProperties(tag, tagDto);
                    return tagDto;
                })
                .collect(Collectors.toSet());
            responseDto.setTags(tagDtos);
        } else {
            responseDto.setTags(new HashSet<>());
        }
        
        // Configurando reviews
        if (product.getReviews() != null) {
            List<ReviewDto> reviewDtos = product.getReviews().stream()
                .map(review -> {
                    ReviewDto reviewDto = new ReviewDto();
                    BeanUtils.copyProperties(review, reviewDto);
                    return reviewDto;
                })
                .collect(Collectors.toList());
            responseDto.setReviews(reviewDtos);
        } else {
            responseDto.setReviews(new ArrayList<>());
        }
        
        // Configurando imageUrls e specifications
        responseDto.setImageUrls(product.getImageUrls() != null ? new ArrayList<>(product.getImageUrls()) : new ArrayList<>());
        responseDto.setSpecifications(product.getSpecifications() != null ? new HashMap<>(product.getSpecifications()) : new HashMap<>());
        
        // Calculando e configurando informações das reviews
        if (product.getId() != null) {
            Double avgRating = reviewRepository.getAverageRatingForProduct(product.getId());
            Integer totalReviews = reviewRepository.getReviewCountForProduct(product.getId());
            responseDto.setAverageRating(avgRating != null ? avgRating : 0.0);
            responseDto.setTotalReviews(totalReviews != null ? totalReviews : 0);
        } else {
            responseDto.setAverageRating(0.0);
            responseDto.setTotalReviews(0);
        }
        
        return responseDto;
    }
}
