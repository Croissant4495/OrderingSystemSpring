package com.ejada.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ejada.project.dto.order.OrderRequestDTO;
import com.ejada.project.dto.order.OrderResponseDTO;
import com.ejada.project.model.Order;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "orderDate", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    Order toEntity(OrderRequestDTO order);
    
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "items", source = "orderItems")
    OrderResponseDTO toResponseDTO(Order order);
}
