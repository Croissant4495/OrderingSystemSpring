package com.ejada.project.service;

import java.util.List;

import com.ejada.project.model.Product;

public interface ProductService {

    List<Product> getAllProducts();

    Product getProductById(Long id);

    Product createProduct(Product product);

    void deleteProduct(Long id);
}
