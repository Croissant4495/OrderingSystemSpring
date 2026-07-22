package com.ejada.project.mapper;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.ejada.project.dto.order.OrderItemRequestDTO;
import com.ejada.project.dto.order.OrderItemResponseDTO;
import com.ejada.project.model.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "priceAtPurchase", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemRequestDTO orderItem);
    

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(orderItem))")
    OrderItemResponseDTO toResponseDTO(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "priceAtPurchase", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "order", ignore = true)
    void updateEntity(OrderItemRequestDTO dto, @MappingTarget OrderItem orderItem);

    default BigDecimal calculateSubtotal(OrderItem item) {
        return item.getPriceAtPurchase()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
    }
}
