package com.ejada.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ejada.project.dto.category.CategoryRequestDTO;
import com.ejada.project.dto.category.CategoryResponseDTO;
import com.ejada.project.model.Category;


@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryRequestDTO category);
    
    CategoryResponseDTO toResponseDTO(Category category);


}
