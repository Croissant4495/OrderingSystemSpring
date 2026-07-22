package com.ejada.project.dto.order;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    @NotNull(message = "User is required")
    private Long userId;

    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemRequestDTO> items;
}
