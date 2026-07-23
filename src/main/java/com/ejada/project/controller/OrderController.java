package com.ejada.project.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ejada.project.dto.order.OrderRequestDTO;
import com.ejada.project.dto.order.OrderResponseDTO;
import com.ejada.project.dto.order.OrderStatusDTO;
import com.ejada.project.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO dto) {
        OrderResponseDTO order = orderService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusDTO dto) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}