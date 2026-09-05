package com.julietamarateo.photography.service;

import com.julietamarateo.photography.dto.OrderDto;
import com.julietamarateo.photography.dto.OrderRequestDto;
import com.julietamarateo.photography.entity.Order;
import com.julietamarateo.photography.entity.OrderItem;
import com.julietamarateo.photography.entity.OrderStatus;
import com.julietamarateo.photography.entity.Photo;
import com.julietamarateo.photography.exception.ResourceNotFoundException;
import com.julietamarateo.photography.repository.OrderRepository;
import com.julietamarateo.photography.repository.PhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final PhotoRepository photoRepository;

    public OrderService(OrderRepository orderRepository, PhotoRepository photoRepository) {
        this.orderRepository = orderRepository;
        this.photoRepository = photoRepository;
    }

    @Transactional
    public OrderDto createOrder(OrderRequestDto request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe contener al menos un ítem");
        }

        String orderId = "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        Order order = new Order(
                orderId,
                request.getCustomerName().trim(),
                request.getCustomerContact().trim(),
                request.getNotes() != null ? request.getNotes().trim() : null
        );

        for (OrderRequestDto.ItemRequest itemReq : request.getItems()) {
            Photo photo = photoRepository.findById(itemReq.getPhotoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Fotografía no encontrada con ID: " + itemReq.getPhotoId()));

            OrderItem orderItem = new OrderItem(
                    photo.getId(),
                    photo.getTitle(),
                    photo.getCategory(),
                    photo.getImageUrl(),
                    itemReq.getQuantity(),
                    photo.getPrice()
            );

            order.addItem(orderItem);
        }

        order.recalculateTotals();
        Order saved = orderRepository.save(order);
        return OrderDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDto getOrderById(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));
        return OrderDto.fromEntity(order);
    }

    @Transactional
    public OrderDto updateOrderStatus(String id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));
        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);
        return OrderDto.fromEntity(updated);
    }
}
