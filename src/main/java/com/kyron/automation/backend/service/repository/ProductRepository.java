package com.kyron.automation.backend.service.repository;

import com.kyron.automation.backend.service.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();
    
    @Query("SELECT p FROM Product p WHERE p.price >= :minPrice AND p.price <= :maxPrice AND p.active = true")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    List<Product> findByNameContainingIgnoreCase(String name);
}
