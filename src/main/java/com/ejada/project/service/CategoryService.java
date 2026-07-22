package com.ejada.project.service;

import java.util.List;

import com.ejada.project.dto.category.CategoryRequestDTO;
import com.ejada.project.dto.category.CategoryResponseDTO;

public interface CategoryService {

    List<CategoryResponseDTO> getAllCategories();

    CategoryResponseDTO getCategoryById(Long id);

    CategoryResponseDTO createCategory(CategoryRequestDTO dto);

    CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO dto);

    void deleteCategory(Long id);
}