package com.ejada.project.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ejada.project.dto.product.ProductRequestDTO;
import com.ejada.project.dto.product.ProductResponseDTO;
import com.ejada.project.exception.ResourceNotFoundException;
import com.ejada.project.mapper.ProductMapper;
import com.ejada.project.model.Category;
import com.ejada.project.model.Product;
import com.ejada.project.repository.CategoryRepository;
import com.ejada.project.repository.ProductRepository;
import com.ejada.project.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toResponseDTO(product);
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        Product product = productMapper.toEntity(dto);
        
        Set<Category> categories = resolveCategories(dto.getCategoryIds());
        product.setCategories(categories);
        
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        productMapper.updateEntity(dto, product);
        
        Set<Category> categories = resolveCategories(dto.getCategoryIds());
        product.setCategories(categories);
        
        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private Set<Category> resolveCategories(Set<Long> categoryIds) {
        Set<Category> categories = new HashSet<>();
        if (categoryIds != null) {
            for (Long categoryId : categoryIds) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
                categories.add(category);
            }
        }
        return categories;
    }
}
