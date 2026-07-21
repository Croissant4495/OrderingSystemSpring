package com.ejada.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ejada.project.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
    
}
