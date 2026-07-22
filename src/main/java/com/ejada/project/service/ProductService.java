package com.ejada.project.service;

import java.util.List;

import com.ejada.project.dto.product.ProductRequestDTO;
import com.ejada.project.dto.product.ProductResponseDTO;

public interface ProductService {

    List<ProductResponseDTO> getAllProducts();

    ProductResponseDTO getProductById(Long id);

    ProductResponseDTO createProduct(ProductRequestDTO dto);

    ProductResponseDTO updateProduct(Long id, ProductRequestDTO dto);

    void deleteProduct(Long id);
}
