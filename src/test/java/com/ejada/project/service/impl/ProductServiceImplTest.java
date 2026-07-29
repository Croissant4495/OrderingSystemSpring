package com.ejada.project.service.impl;

import com.ejada.project.dto.product.ProductRequestDTO;
import com.ejada.project.dto.product.ProductResponseDTO;
import com.ejada.project.exception.BadRequestException;
import com.ejada.project.exception.ResourceNotFoundException;
import com.ejada.project.mapper.ProductMapper;
import com.ejada.project.model.Category;
import com.ejada.project.model.Product;
import com.ejada.project.repository.CategoryRepository;
import com.ejada.project.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private ProductRequestDTO requestDTO;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(100.0));
        testProduct.setStockQuantity(10);
        testProduct.setCategories(new HashSet<>(Set.of(testCategory)));

        requestDTO = new ProductRequestDTO();
        requestDTO.setName("Test Product");
        requestDTO.setPrice(BigDecimal.valueOf(100.0));
        requestDTO.setStockQuantity(10);
        requestDTO.setCategoryIds(Set.of(1L));
    }

    @Test
    void getAllProducts_ShouldReturnPageOfProducts() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(testProduct));
        when(productRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        when(productMapper.toResponseDTO(any(Product.class))).thenReturn(new ProductResponseDTO());

        // Act
        Page<ProductResponseDTO> result = productService.getAllProducts(null, null, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productMapper.toResponseDTO(testProduct)).thenReturn(new ProductResponseDTO());

        // Act
        ProductResponseDTO result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductById_WhenProductDoesNotExist_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(1L));
        verify(productRepository).findById(1L);
    }

    @Test
    void createProduct_WhenValidRequest_ShouldCreateAndReturnProduct() {
        // Arrange
        when(productMapper.toEntity(requestDTO)).thenReturn(testProduct);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productMapper.toResponseDTO(testProduct)).thenReturn(new ProductResponseDTO());

        // Act
        ProductResponseDTO result = productService.createProduct(requestDTO);

        // Assert
        assertNotNull(result);
        verify(categoryRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_WhenInvalidPrice_ShouldThrowException() {
        // Arrange
        requestDTO.setPrice(BigDecimal.valueOf(-10));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> productService.createProduct(requestDTO));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void createProduct_WhenInvalidStock_ShouldThrowException() {
        // Arrange
        requestDTO.setStockQuantity(-5);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> productService.createProduct(requestDTO));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void updateProduct_WhenProductExistsAndValidRequest_ShouldUpdateAndReturnProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        doNothing().when(productMapper).updateEntity(requestDTO, testProduct);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.save(testProduct)).thenReturn(testProduct);
        when(productMapper.toResponseDTO(testProduct)).thenReturn(new ProductResponseDTO());

        // Act
        ProductResponseDTO result = productService.updateProduct(1L, requestDTO);

        // Assert
        assertNotNull(result);
        verify(productMapper).updateEntity(requestDTO, testProduct);
        verify(productRepository).save(testProduct);
    }

    @Test
    void updateProduct_WhenCategoryNotFound_ShouldThrowException() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(1L, requestDTO));
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProduct_WhenExists_ShouldDelete() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_WhenDoesNotExist_ShouldThrowException() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(1L));
        verify(productRepository, never()).deleteById(anyLong());
    }
}
