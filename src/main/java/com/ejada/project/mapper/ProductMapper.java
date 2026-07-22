package com.ejada.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ejada.project.dto.product.ProductRequestDTO;
import com.ejada.project.dto.product.ProductResponseDTO;
import com.ejada.project.model.Product;

@Mapper(componentModel = "spring", uses = CategoryMapper.class)
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Product toEntity(ProductRequestDTO product);
    
    ProductResponseDTO toResponseDTO(Product product);
}
