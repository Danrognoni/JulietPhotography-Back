package com.julietamarateo.photography.controller;

import com.julietamarateo.photography.dto.PreferenceResponseDto;
import com.julietamarateo.photography.entity.Order;
import com.julietamarateo.photography.exception.ResourceNotFoundException;
import com.julietamarateo.photography.repository.OrderRepository;
import com.julietamarateo.photography.service.MercadoPagoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class MercadoPagoController {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoController.class);

    private final MercadoPagoService mercadoPagoService;
    private final OrderRepository orderRepository;

    public MercadoPagoController(MercadoPagoService mercadoPagoService, OrderRepository orderRepository) {
        this.mercadoPagoService = mercadoPagoService;
        this.orderRepository = orderRepository;
    }

    /**
     * Endpoint público para generar o regenerar la preferencia de pago de Checkout Pro para una orden.
     */
    @PostMapping("/api/orders/{id}/preference")
    public ResponseEntity<PreferenceResponseDto> createPreferenceForOrder(@PathVariable String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + id));

        PreferenceResponseDto preferenceDto = mercadoPagoService.createPreference(order);
        return ResponseEntity.ok(preferenceDto);
    }

    /**
     * Webhook oficial para recibir notificaciones IPN de Mercado Pago.
     * Siempre responde HTTP 200 OK inmediatamente para evitar reintentos de entrega.
     */
    @PostMapping("/api/mercadopago/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "topic", required = false) String topic,
            @RequestParam(value = "data.id", required = false) String dataId,
            @RequestParam(value = "id", required = false) String paramId,
            @RequestBody(required = false) Map<String, Object> body) {

        log.info("Webhook recibido de Mercado Pago: type={}, topic={}, dataId={}, paramId={}, body={}",
                type, topic, dataId, paramId, body);

        String paymentId = null;

        if (dataId != null && !dataId.isBlank()) {
            paymentId = dataId.trim();
        } else if (paramId != null && !paramId.isBlank()) {
            paymentId = paramId.trim();
        } else if (body != null) {
            if (body.get("data") instanceof Map<?, ?> dataMap && dataMap.get("id") != null) {
                paymentId = String.valueOf(dataMap.get("id")).trim();
            } else if (body.get("id") != null) {
                paymentId = String.valueOf(body.get("id")).trim();
            }
        }

        // Validar si la notificación es de pago
        boolean isPayment = "payment".equalsIgnoreCase(type)
                || "payment".equalsIgnoreCase(topic)
                || (body != null && "payment".equalsIgnoreCase(String.valueOf(body.get("type"))))
                || (body != null && String.valueOf(body.get("action")).startsWith("payment."));

        if (paymentId != null && (isPayment || (type == null && topic == null))) {
            try {
                mercadoPagoService.processPaymentNotification(paymentId);
            } catch (Exception e) {
                log.error("Error al procesar notificación de pago ID {}: {}", paymentId, e.getMessage());
            }
        } else {
            log.info("Notificación recibida no requiere procesamiento de pago directo (topic/type={} o ID ausente)",
                    topic != null ? topic : type);
        }

        // Siempre responder 200 OK a Mercado Pago para confirmar recepción
        return ResponseEntity.ok().build();
    }
}
