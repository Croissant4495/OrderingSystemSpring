package com.ejada.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.ejada.project.dto.order.OrderResponseDTO;
import com.ejada.project.model.Order;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "items", source = "orderItems")
    OrderResponseDTO toResponseDTO(Order order);
}
