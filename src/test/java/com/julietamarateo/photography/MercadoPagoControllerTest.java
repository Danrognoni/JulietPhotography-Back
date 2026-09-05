package com.julietamarateo.photography;

import com.julietamarateo.photography.dto.PreferenceResponseDto;
import com.julietamarateo.photography.entity.Order;
import com.julietamarateo.photography.entity.OrderItem;
import com.julietamarateo.photography.entity.PaymentRecord;
import com.julietamarateo.photography.repository.OrderRepository;
import com.julietamarateo.photography.repository.PaymentRecordRepository;
import com.julietamarateo.photography.service.MercadoPagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(MercadoPagoControllerTest.MockMercadoPagoConfig.class)
public class MercadoPagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRecordRepository paymentRecordRepository;

    private Order testOrder;

    @TestConfiguration
    static class MockMercadoPagoConfig {
        @Bean
        @Primary
        public MercadoPagoService testMercadoPagoService(OrderRepository orderRepo, PaymentRecordRepository paymentRepo) {
            return new MercadoPagoService(orderRepo, paymentRepo) {
                @Override
                public PreferenceResponseDto createPreference(Order order) {
                    if (order == null || order.getItems() == null || order.getItems().isEmpty()) {
                        throw new IllegalArgumentException("Orden inválida");
                    }
                    return new PreferenceResponseDto(
                            "pref-123456789",
                            "https://www.mercadopago.com.ar/checkout/v1/redirect?pref_id=pref-123456789",
                            "https://sandbox.mercadopago.com.ar/checkout/v1/redirect?pref_id=pref-123456789"
                    );
                }

                @Override
                public PaymentRecord processPaymentNotification(String paymentId) {
                    PaymentRecord rec = new PaymentRecord();
                    rec.setOrderId("ORD-TEST-MP-101");
                    rec.setMercadopagoPaymentId(paymentId);
                    rec.setStatus("approved");
                    rec.setTransactionAmount(150.0);
                    return rec;
                }
            };
        }
    }

    @BeforeEach
    void setUp() {
        testOrder = new Order("ORD-TEST-MP-101", "Marina Gomez", "marina@gmail.com", "Entrega urgente");
        OrderItem item = new OrderItem("photo-1", "Amanecer en los Acantilados", "Paisajismo", "http://img.jpg", 1, 150.0);
        testOrder.addItem(item);
        testOrder.recalculateTotals();
        orderRepository.save(testOrder);
    }

    @Test
    @DisplayName("Preferencia de Pago: POST /api/orders/{id}/preference genera los links de checkout (200 OK)")
    void testCreatePreferenceEndpointSuccess() throws Exception {
        mockMvc.perform(post("/api/orders/ORD-TEST-MP-101/preference")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferenceId").value("pref-123456789"))
                .andExpect(jsonPath("$.initPoint").value(org.hamcrest.Matchers.containsString("mercadopago.com.ar")))
                .andExpect(jsonPath("$.sandboxInitPoint").value(org.hamcrest.Matchers.containsString("sandbox.mercadopago")));
    }

    @Test
    @DisplayName("Preferencia de Pago: POST /api/orders/{id}/preference con ID inexistente devuelve 404")
    void testCreatePreferenceNotFound() throws Exception {
        mockMvc.perform(post("/api/orders/ORD-INEXISTENTE-999/preference")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Webhook IPN: POST /api/mercadopago/webhook con query params procesa pago y retorna 200 OK")
    void testWebhookWithQueryParams() throws Exception {
        mockMvc.perform(post("/api/mercadopago/webhook")
                        .param("type", "payment")
                        .param("data.id", "9876543210"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Webhook IPN: POST /api/mercadopago/webhook con JSON payload procesa pago y retorna 200 OK")
    void testWebhookWithJsonPayload() throws Exception {
        String jsonPayload = """
                {
                    "action": "payment.created",
                    "type": "payment",
                    "data": {
                        "id": "12345678"
                    }
                }
                """;

        mockMvc.perform(post("/api/mercadopago/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Persistencia PaymentRecord: verifica guardado en base de datos SQLite")
    void testPaymentRecordRepository() {
        String testPaymentId = "MP-PAY-" + System.currentTimeMillis();
        PaymentRecord record = new PaymentRecord(
                "ORD-TEST-MP-101",
                testPaymentId,
                "approved",
                "accredited",
                "credit_card",
                300.0,
                280.0,
                LocalDateTime.now()
        );

        PaymentRecord saved = paymentRecordRepository.save(record);
        org.junit.jupiter.api.Assertions.assertNotNull(saved.getId());
        org.junit.jupiter.api.Assertions.assertEquals(testPaymentId, saved.getMercadopagoPaymentId());
        org.junit.jupiter.api.Assertions.assertEquals("approved", saved.getStatus());
    }
}
