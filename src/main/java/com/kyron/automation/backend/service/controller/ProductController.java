package com.kyron.automation.backend.service.controller;

import com.kyron.automation.backend.service.dto.ProductRequestDto;
import com.kyron.automation.backend.service.dto.ProductResponseDto;
import com.kyron.automation.backend.service.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // POST endpoint with request body validation and custom headers
    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestBody ProductRequestDto product,
            @RequestHeader(value = "X-API-Key", required = true) String apiKey) {
        ProductResponseDto createdProduct = productService.createProduct(product);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Resource-ID", createdProduct.getId().toString());
        return new ResponseEntity<>(createdProduct, headers, HttpStatus.CREATED);
    }

    // GET endpoint with path variable
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    // GET endpoint with optional query parameters and pagination
    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts(
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // PUT endpoint with path variable and request body
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    // PATCH endpoint for partial updates
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        // Implementation would go here
        return ResponseEntity.noContent().build();
    }

    // DELETE endpoint
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // GET endpoint with multiple query parameters
    @GetMapping("/search/price")
    public ResponseEntity<List<ProductResponseDto>> searchByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        return ResponseEntity.ok(productService.searchByPriceRange(minPrice, maxPrice));
    }

    // GET endpoint with query parameter
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchByName(
            @RequestParam String name) {
        return ResponseEntity.ok(productService.searchByName(name));
    }

    // HEAD endpoint to check resource existence
    @RequestMapping(method = RequestMethod.HEAD, value = "/{id}")
    public ResponseEntity<Void> checkProductExists(@PathVariable Long id) {
        productService.getProduct(id); // Will throw EntityNotFoundException if not found
        return ResponseEntity.ok().build();
    }

    // Endpoint para adicionar tags a um produto
    @PostMapping("/{id}/tags")
    public ResponseEntity<ProductResponseDto> addTags(
            @PathVariable Long id,
            @RequestBody Set<Long> tagIds) {
        return ResponseEntity.ok(productService.addTags(id, tagIds));
    }

    // Endpoint para adicionar especificações a um produto
    @PostMapping("/{id}/specifications")
    public ResponseEntity<ProductResponseDto> addSpecifications(
            @PathVariable Long id,
            @RequestBody Map<String, String> specifications) {
        return ResponseEntity.ok(productService.addSpecifications(id, specifications));
    }

    // Endpoint para adicionar URLs de imagens a um produto
    @PostMapping("/{id}/images")
    public ResponseEntity<ProductResponseDto> addImages(
            @PathVariable Long id,
            @RequestBody List<String> imageUrls) {
        return ResponseEntity.ok(productService.addImages(id, imageUrls));
    }

    // Endpoint para buscar produtos por categoria
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    // Endpoint para buscar produtos por tag
    @GetMapping("/tag/{tagId}")
    public ResponseEntity<List<ProductResponseDto>> getProductsByTag(
            @PathVariable Long tagId) {
        return ResponseEntity.ok(productService.getProductsByTag(tagId));
    }

    // Endpoint para buscar produtos com filtros complexos
    @GetMapping("/search/advanced")
    public ResponseEntity<List<ProductResponseDto>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Set<Long> tagIds,
            @RequestParam(required = false) Double minRating) {
        return ResponseEntity.ok(productService.searchProducts(name, minPrice, maxPrice, categoryId, tagIds, minRating));
    }

    // OPTIONS endpoint to show allowed methods
    @RequestMapping(method = RequestMethod.OPTIONS)
    public ResponseEntity<Void> getOptions() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Allow", "GET,POST,PUT,DELETE,HEAD,OPTIONS,PATCH");
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}
