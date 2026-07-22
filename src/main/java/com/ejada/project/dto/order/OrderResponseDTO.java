package com.ejada.project.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.ejada.project.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDTO {
    private Long id;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private Long userId;
    private String username;
    private List<OrderItemResponseDTO> items;
}