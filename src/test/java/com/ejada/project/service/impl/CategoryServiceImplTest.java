package com.ejada.project.service.impl;

import com.ejada.project.dto.category.CategoryRequestDTO;
import com.ejada.project.dto.category.CategoryResponseDTO;
import com.ejada.project.exception.ResourceAlreadyExistsException;
import com.ejada.project.exception.ResourceNotFoundException;
import com.ejada.project.mapper.CategoryMapper;
import com.ejada.project.model.Category;
import com.ejada.project.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;
    private CategoryRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Electronics");

        requestDTO = new CategoryRequestDTO();
        requestDTO.setName("Electronics");
    }

    @Test
    void getAllCategories_ShouldReturnListOfCategories() {
        // Arrange
        when(categoryRepository.findAll()).thenReturn(List.of(testCategory));
        when(categoryMapper.toResponseDTO(testCategory)).thenReturn(new CategoryResponseDTO());

        // Act
        List<CategoryResponseDTO> result = categoryService.getAllCategories();

        // Assert
        assertEquals(1, result.size());
        verify(categoryRepository).findAll();
    }

    @Test
    void getCategoryById_WhenCategoryExists_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryMapper.toResponseDTO(testCategory)).thenReturn(new CategoryResponseDTO());

        // Act
        CategoryResponseDTO result = categoryService.getCategoryById(1L);

        // Assert
        assertNotNull(result);
        verify(categoryRepository).findById(1L);
    }

    @Test
    void getCategoryById_WhenCategoryDoesNotExist_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(1L));
        verify(categoryRepository).findById(1L);
    }

    @Test
    void createCategory_WhenValidRequest_ShouldCreateAndReturnCategory() {
        // Arrange
        when(categoryRepository.findByName(requestDTO.getName())).thenReturn(Optional.empty());
        when(categoryMapper.toEntity(requestDTO)).thenReturn(testCategory);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
        when(categoryMapper.toResponseDTO(testCategory)).thenReturn(new CategoryResponseDTO());

        // Act
        CategoryResponseDTO result = categoryService.createCategory(requestDTO);

        // Assert
        assertNotNull(result);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_WhenDuplicateName_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findByName(requestDTO.getName())).thenReturn(Optional.of(testCategory));

        // Act & Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> categoryService.createCategory(requestDTO));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void updateCategory_WhenCategoryExistsAndValidRequest_ShouldUpdateAndReturnCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.findByName(requestDTO.getName())).thenReturn(Optional.empty());
        doNothing().when(categoryMapper).updateEntity(requestDTO, testCategory);
        when(categoryRepository.save(testCategory)).thenReturn(testCategory);
        when(categoryMapper.toResponseDTO(testCategory)).thenReturn(new CategoryResponseDTO());

        // Act
        CategoryResponseDTO result = categoryService.updateCategory(1L, requestDTO);

        // Assert
        assertNotNull(result);
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void updateCategory_WhenDuplicateName_ShouldThrowException() {
        // Arrange
        Category anotherCategory = new Category();
        anotherCategory.setId(2L);
        anotherCategory.setName("Electronics");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.findByName(requestDTO.getName())).thenReturn(Optional.of(anotherCategory));

        // Act & Assert
        assertThrows(ResourceAlreadyExistsException.class, () -> categoryService.updateCategory(1L, requestDTO));
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void deleteCategory_WhenExists_ShouldDelete() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void deleteCategory_WhenDoesNotExist_ShouldThrowException() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository, never()).deleteById(anyLong());
    }
}
