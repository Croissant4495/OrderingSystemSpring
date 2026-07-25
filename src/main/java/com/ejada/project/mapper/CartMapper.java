package com.ejada.project.mapper;

import java.math.BigDecimal;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.ejada.project.dto.cart.CartItemResponseDTO;
import com.ejada.project.dto.cart.CartResponseDTO;
import com.ejada.project.model.Cart;
import com.ejada.project.model.CartItem;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "items", source = "cartItems")
    @Mapping(target = "totalCartPrice", source = "cart", qualifiedByName = "calculateTotalCartPrice")
    CartResponseDTO toResponseDTO(Cart cart);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "unitPrice", source = "product.price")
    @Mapping(target = "subtotal", source = "cartItem", qualifiedByName = "calculateSubtotal")
    CartItemResponseDTO toItemResponseDTO(CartItem cartItem);

    @Named("calculateTotalCartPrice")
    default BigDecimal calculateTotalCartPrice(Cart cart) {
        if (cart == null || cart.getCartItems() == null) {
            return BigDecimal.ZERO;
        }
        return cart.getCartItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Named("calculateSubtotal")
    default BigDecimal calculateSubtotal(CartItem cartItem) {
        if (cartItem == null || cartItem.getProduct() == null || cartItem.getQuantity() == null) {
            return BigDecimal.ZERO;
        }
        return cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
    }
}
