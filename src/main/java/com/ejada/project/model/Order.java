package com.ejada.project.model;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ejada.project.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name= "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "order_seq")
    @SequenceGenerator(
        name = "order_seq",
        sequenceName = "ORDER_SEQ",
        allocationSize = 1)    
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderDate;
    
    private BigDecimal totalAmount;

    @OneToMany(
        mappedBy = "order",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<OrderItem> orderItems = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name= "user_id", nullable = false)
    private User user;
}

