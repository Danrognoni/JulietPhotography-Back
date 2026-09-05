package com.julietamarateo.photography.controller;

import com.julietamarateo.photography.dto.OrderDto;
import com.julietamarateo.photography.dto.OrderRequestDto;
import com.julietamarateo.photography.entity.OrderStatus;
import com.julietamarateo.photography.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Endpoint público para registrar un pedido/adquisición desde el carrito.
     */
    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderRequestDto request) {
        OrderDto created = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Endpoint protegido para que la administradora consulte todos los pedidos.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Endpoint para consultar el detalle de un pedido por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable String id) {
        OrderDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * Endpoint protegido para actualizar el estado de una orden.
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable String id,
            @RequestParam OrderStatus status) {
        OrderDto updated = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}
