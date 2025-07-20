package com.kyron.automation.backend.service.repository;

import com.kyron.automation.backend.service.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByActiveTrue();
    List<Category> findByNameContainingIgnoreCase(String name);
}
