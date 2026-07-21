package com.ejada.project.model;

import java.util.HashSet;
import java.util.Set;

import com.ejada.project.enums.CategoryName;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name= "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_seq")
    @SequenceGenerator(
        name = "category_seq",
        sequenceName = "CATEGORY_SEQ",
        allocationSize = 1)    
    private Long id;

    @Enumerated(EnumType.STRING)
    private CategoryName name;

    @ManyToMany(mappedBy = "categories")
    private Set<Product> products = new HashSet<>();
}
