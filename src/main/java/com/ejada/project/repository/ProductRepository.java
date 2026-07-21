package com.ejada.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ejada.project.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
    
}
