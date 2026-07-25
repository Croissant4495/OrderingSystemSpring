package com.ejada.project.service.impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.ejada.project.dto.product.ProductRequestDTO;
import com.ejada.project.dto.product.ProductResponseDTO;
import com.ejada.project.exception.BadRequestException;
import com.ejada.project.exception.ResourceNotFoundException;
import com.ejada.project.mapper.ProductMapper;
import com.ejada.project.model.Category;
import com.ejada.project.model.Product;
import com.ejada.project.repository.CategoryRepository;
import com.ejada.project.repository.ProductRepository;
import com.ejada.project.service.ProductService;
import com.ejada.project.specification.ProductSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<ProductResponseDTO> getAllProducts(
            String search,
            String category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            Pageable pageable) {
        
        Specification<Product> spec = ProductSpecification.filterProducts(
                search, category, minPrice, maxPrice, inStock);

        return productRepository.findAll(spec, pageable)
                .map(productMapper::toResponseDTO);
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return productMapper.toResponseDTO(product);
    }

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {
        validatePriceAndStock(dto.getPrice(), dto.getStockQuantity());

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

        validatePriceAndStock(dto.getPrice(), dto.getStockQuantity());

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

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Enforces business rules for price and stock in the service layer, independent
     * of DTO bean-validation annotations.
     */
    private void validatePriceAndStock(BigDecimal price, Integer stockQuantity) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Product price must be greater than zero.");
        }
        if (stockQuantity == null || stockQuantity < 0) {
            throw new BadRequestException("Product stock quantity cannot be negative.");
        }
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
