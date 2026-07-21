package com.ejada.project.service;

import java.util.List;

import com.ejada.project.model.Category;

public interface CategoryService {

    List<Category> getAllCategories();

    Category getCategoryById(Long id);

    Category createCategory(Category category);

    void deleteCategory(Long id);
}