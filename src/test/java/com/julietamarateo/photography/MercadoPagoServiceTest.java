package com.julietamarateo.photography;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.julietamarateo.photography.dto.PreferenceResponseDto;
import com.julietamarateo.photography.entity.Order;
import com.julietamarateo.photography.entity.OrderItem;
import com.julietamarateo.photography.repository.OrderRepository;
import com.julietamarateo.photography.repository.PaymentRecordRepository;
import com.julietamarateo.photography.service.MercadoPagoService;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class MercadoPagoServiceTest {

    private OrderRepository orderRepository;
    private PaymentRecordRepository paymentRecordRepository;
    private MercadoPagoService mercadoPagoService;

    private static class TestPreferenceClient extends PreferenceClient {
        PreferenceRequest capturedRequest;
        Preference stubPreference;

        @Override
        public Preference create(PreferenceRequest request) {
            this.capturedRequest = request;
            return stubPreference;
        }
    }

    private TestPreferenceClient testPreferenceClient;

    @BeforeEach
    void setUp() throws Exception {
        orderRepository = mock(OrderRepository.class);
        paymentRecordRepository = mock(PaymentRecordRepository.class);
        testPreferenceClient = new TestPreferenceClient();

        String json = "{\"id\":\"pref-123456\",\"initPoint\":\"https://www.mercadopago.com.ar/checkout/v1/redirect?pref_id=pref-123456\",\"sandboxInitPoint\":\"https://sandbox.mercadopago.com.ar/checkout/v1/redirect?pref_id=pref-123456\"}";
        testPreferenceClient.stubPreference = new ObjectMapper().readValue(json, Preference.class);

        mercadoPagoService = new MercadoPagoService(orderRepository, paymentRecordRepository) {
            @Override
            protected PreferenceClient createPreferenceClient() {
                return testPreferenceClient;
            }
        };

        ReflectionTestUtils.setField(mercadoPagoService, "backUrlSuccess", "https://juli-fotografia-front.vercel.app/?status=approved");
        ReflectionTestUtils.setField(mercadoPagoService, "backUrlFailure", "https://juli-fotografia-front.vercel.app/?status=rejected");
        ReflectionTestUtils.setField(mercadoPagoService, "backUrlPending", "https://juli-fotografia-front.vercel.app/?status=pending");
        ReflectionTestUtils.setField(mercadoPagoService, "notificationUrl", "https://tu-dominio.com/api/mercadopago/webhook");
    }

    @Test
    @DisplayName("createPreference debe construir PreferenceRequest con autoReturn approved y con backUrls sanitizadas")
    void testCreatePreferenceWithAutoReturnApproved() {
        Order order = new Order("ORD-TEST-123", "Cliente Test", "test@example.com", "Notas");
        order.addItem(new OrderItem("photo-1", "Foto Montaña", "Naturaleza", "http://img.jpg", 2, 2500.0));

        PreferenceResponseDto response = mercadoPagoService.createPreference(order);

        assertNotNull(response);
        assertEquals("pref-123456", response.getPreferenceId());
        assertEquals("https://www.mercadopago.com.ar/checkout/v1/redirect?pref_id=pref-123456", response.getInitPoint());
        assertEquals("https://sandbox.mercadopago.com.ar/checkout/v1/redirect?pref_id=pref-123456", response.getSandboxInitPoint());

        PreferenceRequest capturedRequest = testPreferenceClient.capturedRequest;
        assertNotNull(capturedRequest);
        assertEquals("approved", capturedRequest.getAutoReturn(), "autoReturn debe ser 'approved'");
        assertNotNull(capturedRequest.getBackUrls());
        assertEquals("https://juli-fotografia-front.vercel.app/?status=approved", capturedRequest.getBackUrls().getSuccess());
        assertEquals("https://juli-fotografia-front.vercel.app/?status=rejected", capturedRequest.getBackUrls().getFailure());
        assertEquals("https://juli-fotografia-front.vercel.app/?status=pending", capturedRequest.getBackUrls().getPending());
        assertEquals("ORD-TEST-123", capturedRequest.getExternalReference());
        assertEquals(1, capturedRequest.getItems().size());
        assertNull(capturedRequest.getNotificationUrl(), "notificationUrl con tu-dominio.com debe ser omitida");
    }

    @Test
    @DisplayName("createPreference debe incluir notificationUrl si es una URL válida externa")
    void testCreatePreferenceWithValidNotificationUrl() {
        ReflectionTestUtils.setField(mercadoPagoService, "notificationUrl", "https://api.miestudiofotografico.com/api/mercadopago/webhook");

        Order order = new Order("ORD-TEST-456", "Cliente Valido", "cliente@example.com", "Notas");
        order.addItem(new OrderItem("photo-2", "Foto Retrato", "Retrato", "http://img2.jpg", 1, 1000.0));

        mercadoPagoService.createPreference(order);

        PreferenceRequest capturedRequest = testPreferenceClient.capturedRequest;
        assertNotNull(capturedRequest);
        assertEquals("https://api.miestudiofotografico.com/api/mercadopago/webhook", capturedRequest.getNotificationUrl());
        assertEquals("approved", capturedRequest.getAutoReturn());
    }

    @Test
    @DisplayName("createPreference debe lanzar IllegalArgumentException si la orden es nula o no tiene items")
    void testCreatePreferenceValidation() {
        Order emptyOrder = new Order("ORD-EMPTY", "Cliente", "email@test.com", "");
        assertThrows(IllegalArgumentException.class, () -> mercadoPagoService.createPreference(emptyOrder));
        assertThrows(IllegalArgumentException.class, () -> mercadoPagoService.createPreference(null));
    }

    @Test
    @DisplayName("createPreference debe lanzar IllegalArgumentException si algún ítem tiene precio nulo o menor/igual a cero")
    void testCreatePreferenceItemPriceValidation() {
        Order invalidOrder = new Order("ORD-INVALID", "Cliente", "email@test.com", "");
        invalidOrder.addItem(new OrderItem("photo-free", "Foto Gratis", "Naturaleza", "http://img.jpg", 1, 0.0));
        assertThrows(IllegalArgumentException.class, () -> mercadoPagoService.createPreference(invalidOrder));
    }
}
