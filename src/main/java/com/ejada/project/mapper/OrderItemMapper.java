package com.ejada.project.mapper;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.ejada.project.dto.order.OrderItemResponseDTO;
import com.ejada.project.model.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(orderItem))")
    OrderItemResponseDTO toResponseDTO(OrderItem orderItem);

    default BigDecimal calculateSubtotal(OrderItem item) {
        return item.getPriceAtPurchase()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
    }
}
