package com.ejada.project.dto.order;

import com.ejada.project.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusDTO {

    @NotNull(message = "Order status is required")
    private OrderStatus status;
}
