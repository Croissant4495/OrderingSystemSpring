package com.ejada.project.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name= "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "orderitem_seq")
    @SequenceGenerator(
        name = "orderitem_seq",
        sequenceName = "ORDERITEM_SEQ",
        allocationSize = 1)    
    private Long id;

    private Integer quantity;

    private BigDecimal priceAtPurchase;

    @ManyToOne
    @JoinColumn(name="product_id", nullable=false)
    private Product product;

    @ManyToOne
    @JoinColumn(name="order_id", nullable=false)
    private Order order;

}
