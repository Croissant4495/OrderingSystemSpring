package com.ejada.project.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name= "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prod_seq")
    @SequenceGenerator(
        name = "prod_seq",
        sequenceName = "PROD_SEQ",
        allocationSize = 1)    
    private Long id;

    private String name;
    private String description;

    private BigDecimal price;
    private Integer stockQuantity;

    @ManyToMany
    @JoinTable(
        name = "PRODUCT_CATEGORIES",
        joinColumns = @JoinColumn(name = "PROD_ID"),
        inverseJoinColumns = @JoinColumn(name = "CAT_ID")
    )
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems = new ArrayList<>();
}
