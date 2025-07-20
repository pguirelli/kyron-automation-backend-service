package com.kyron.automation.backend.service.service;

import com.kyron.automation.backend.service.dto.CategoryDto;
import com.kyron.automation.backend.service.model.Category;
import com.kyron.automation.backend.service.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDto, category);
        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory);
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findByActiveTrue().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        return convertToDto(category);
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        BeanUtils.copyProperties(categoryDto, category, "id");
        Category updatedCategory = categoryRepository.save(category);
        return convertToDto(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id: " + id));
        category.setActive(false);
        categoryRepository.save(category);
    }

    private CategoryDto convertToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }
}
