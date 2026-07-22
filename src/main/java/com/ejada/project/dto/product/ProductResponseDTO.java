package com.ejada.project.dto.product;

import java.math.BigDecimal;
import java.util.Set;

import com.ejada.project.dto.category.CategoryResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Set<CategoryResponseDTO> categories;
}