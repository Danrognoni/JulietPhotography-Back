package com.julietamarateo.photography.service;

import com.julietamarateo.photography.dto.PreferenceResponseDto;
import com.julietamarateo.photography.entity.Order;
import com.julietamarateo.photography.entity.OrderItem;
import com.julietamarateo.photography.entity.OrderStatus;
import com.julietamarateo.photography.entity.PaymentRecord;
import com.julietamarateo.photography.exception.ResourceNotFoundException;
import com.julietamarateo.photography.repository.OrderRepository;
import com.julietamarateo.photography.repository.PaymentRecordRepository;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MercadoPagoService {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoService.class);

    private final OrderRepository orderRepository;
    private final PaymentRecordRepository paymentRecordRepository;

    @Value("${mercadopago.back-urls.success:http://localhost:4200/cart?status=approved}")
    private String backUrlSuccess;

    @Value("${mercadopago.back-urls.failure:http://localhost:4200/cart?status=rejected}")
    private String backUrlFailure;

    @Value("${mercadopago.back-urls.pending:http://localhost:4200/cart?status=pending}")
    private String backUrlPending;

    @Value("${mercadopago.notification-url:https://tu-dominio.com/api/mercadopago/webhook}")
    private String notificationUrl;

    public MercadoPagoService(OrderRepository orderRepository, PaymentRecordRepository paymentRecordRepository) {
        this.orderRepository = orderRepository;
        this.paymentRecordRepository = paymentRecordRepository;
    }

    /**
     * Crea una preferencia de pago en Mercado Pago para una orden existente.
     */
    @Transactional(readOnly = true)
    public PreferenceResponseDto createPreference(Order order) {
        if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("La orden debe existir y contener ítems para generar la preferencia de pago");
        }

        List<PreferenceItemRequest> items = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(item.getPhotoId())
                    .title(item.getPhotoTitle() != null ? item.getPhotoTitle() : "Fotografía Juliet Photography")
                    .quantity(item.getQuantity())
                    .unitPrice(BigDecimal.valueOf(item.getUnitPrice()))
                    .currencyId("ARS")
                    .build();
            items.add(itemRequest);
        }

        // Asegurar URLs válidas incluso si faltan en application.properties
        String successUrl = (backUrlSuccess != null && !backUrlSuccess.isBlank())
                ? backUrlSuccess.trim()
                : "http://localhost:4200/cart?status=approved";

        String failureUrl = (backUrlFailure != null && !backUrlFailure.isBlank())
                ? backUrlFailure.trim()
                : "http://localhost:4200/cart?status=rejected";

        String pendingUrl = (backUrlPending != null && !backUrlPending.isBlank())
                ? backUrlPending.trim()
                : "http://localhost:4200/cart?status=pending";

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success(successUrl)
                .failure(failureUrl)
                .pending(pendingUrl)
                .build();

        PreferenceRequest.PreferenceRequestBuilder requestBuilder = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .autoReturn("approved")
                .externalReference(order.getId());

        if (notificationUrl != null && !notificationUrl.isBlank() && !notificationUrl.contains("tu-dominio.com")) {
            requestBuilder.notificationUrl(notificationUrl.trim());
        }

        PreferenceRequest request = requestBuilder.build();

        try {
            PreferenceClient client = createPreferenceClient();
            Preference preference = client.create(request);

            log.info("Preferencia Mercado Pago generada con éxito para la orden {}: ID={}", order.getId(), preference.getId());
            return new PreferenceResponseDto(
                    preference.getId(),
                    preference.getInitPoint(),
                    preference.getSandboxInitPoint()
            );
        } catch (MPApiException apiEx) {
            log.error("Error API de Mercado Pago al crear preferencia: status={}, mensaje={}",
                    apiEx.getStatusCode(), apiEx.getApiResponse() != null ? apiEx.getApiResponse().getContent() : apiEx.getMessage());
            throw new RuntimeException("Error en comunicación con Mercado Pago: " + apiEx.getMessage(), apiEx);
        } catch (MPException mpEx) {
            log.error("Error de cliente Mercado Pago: {}", mpEx.getMessage());
            throw new RuntimeException("Error al procesar preferencia en Mercado Pago: " + mpEx.getMessage(), mpEx);
        }
    }

    /**
     * Procesa la notificación webhook IPN de un pago de Mercado Pago.
     * Consulta el pago oficial en la API de Mercado Pago, actualiza el estado de la orden y persiste el comprobante.
     */
    @Transactional
    public PaymentRecord processPaymentNotification(String paymentIdStr) {
        if (paymentIdStr == null || paymentIdStr.isBlank()) {
            throw new IllegalArgumentException("El ID de pago de Mercado Pago no puede ser nulo o vacío");
        }

        Long paymentId;
        try {
            paymentId = Long.parseLong(paymentIdStr.trim());
        } catch (NumberFormatException e) {
            log.error("ID de pago con formato inválido: {}", paymentIdStr);
            throw new IllegalArgumentException("ID de pago inválido: " + paymentIdStr);
        }

        try {
            PaymentClient client = createPaymentClient();
            Payment payment = client.get(paymentId);

            if (payment == null) {
                log.warn("Mercado Pago no retornó datos para el pago ID {}", paymentId);
                return null;
            }

            String orderId = payment.getExternalReference();
            String status = payment.getStatus();
            String statusDetail = payment.getStatusDetail();
            String paymentMethod = payment.getPaymentMethodId();
            Double transactionAmount = payment.getTransactionAmount() != null ? payment.getTransactionAmount().doubleValue() : null;
            Double netReceivedAmount = (payment.getTransactionDetails() != null && payment.getTransactionDetails().getNetReceivedAmount() != null)
                    ? payment.getTransactionDetails().getNetReceivedAmount().doubleValue()
                    : null;
            LocalDateTime dateApproved = payment.getDateApproved() != null ? payment.getDateApproved().toLocalDateTime() : null;

            log.info("Webhook recibido de Mercado Pago: paymentId={}, status={}, orderId={}", paymentId, status, orderId);

            // Actualizar estado de la Orden si existe
            if (orderId != null && !orderId.isBlank()) {
                orderRepository.findById(orderId).ifPresentOrElse(order -> {
                    if ("approved".equalsIgnoreCase(status)) {
                        order.setStatus(OrderStatus.CONFIRMED);
                        orderRepository.save(order);
                        log.info("Orden {} actualizada a CONFIRMED tras pago aprobado", orderId);
                    } else if ("rejected".equalsIgnoreCase(status) || "cancelled".equalsIgnoreCase(status)) {
                        order.setStatus(OrderStatus.CANCELLED);
                        orderRepository.save(order);
                        log.info("Orden {} actualizada a CANCELLED tras pago rechazado o cancelado", orderId);
                    }
                }, () -> log.warn("Orden con ID {} no encontrada para asociar el pago {}", orderId, paymentId));
            }

            // Registrar o actualizar PaymentRecord
            PaymentRecord paymentRecord = paymentRecordRepository.findByMercadopagoPaymentId(String.valueOf(paymentId))
                    .orElseGet(PaymentRecord::new);

            paymentRecord.setOrderId(orderId != null ? orderId : "UNKNOWN");
            paymentRecord.setMercadopagoPaymentId(String.valueOf(paymentId));
            paymentRecord.setStatus(status != null ? status : "unknown");
            paymentRecord.setStatusDetail(statusDetail);
            paymentRecord.setPaymentMethod(paymentMethod);
            paymentRecord.setTransactionAmount(transactionAmount);
            paymentRecord.setNetReceivedAmount(netReceivedAmount);
            paymentRecord.setDateApproved(dateApproved);

            PaymentRecord saved = paymentRecordRepository.save(paymentRecord);
            log.info("PaymentRecord registrado con ID: {}", saved.getId());
            return saved;
        } catch (MPApiException apiEx) {
            log.error("Error API al verificar pago {}: status={}, respuesta={}",
                    paymentId, apiEx.getStatusCode(), apiEx.getApiResponse() != null ? apiEx.getApiResponse().getContent() : apiEx.getMessage());
            throw new RuntimeException("Error API Mercado Pago al verificar pago: " + apiEx.getMessage(), apiEx);
        } catch (MPException mpEx) {
            log.error("Error de cliente Mercado Pago al verificar pago {}: {}", paymentId, mpEx.getMessage());
            throw new RuntimeException("Error al consultar pago en Mercado Pago: " + mpEx.getMessage(), mpEx);
        }
    }

    // Métodos factoría para permitir pruebas unitarias y mocks
    protected PreferenceClient createPreferenceClient() {
        return new PreferenceClient();
    }

    protected PaymentClient createPaymentClient() {
        return new PaymentClient();
    }
}
