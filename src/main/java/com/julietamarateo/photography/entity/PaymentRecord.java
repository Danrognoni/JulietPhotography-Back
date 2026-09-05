package com.julietamarateo.photography.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payments_order_id", columnList = "orderId"),
    @Index(name = "idx_payments_mp_id", columnList = "mercadopagoPaymentId")
})
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String orderId;

    @Column(nullable = false, unique = true, length = 64)
    private String mercadopagoPaymentId;

    @Column(nullable = false, length = 32)
    private String status;

    private String statusDetail;

    private String paymentMethod;

    private Double transactionAmount;

    private Double netReceivedAmount;

    private LocalDateTime dateApproved;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public PaymentRecord() {
    }

    public PaymentRecord(String orderId, String mercadopagoPaymentId, String status,
                         String statusDetail, String paymentMethod, Double transactionAmount,
                         Double netReceivedAmount, LocalDateTime dateApproved) {
        this.orderId = orderId;
        this.mercadopagoPaymentId = mercadopagoPaymentId;
        this.status = status;
        this.statusDetail = statusDetail;
        this.paymentMethod = paymentMethod;
        this.transactionAmount = transactionAmount;
        this.netReceivedAmount = netReceivedAmount;
        this.dateApproved = dateApproved;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getMercadopagoPaymentId() {
        return mercadopagoPaymentId;
    }

    public void setMercadopagoPaymentId(String mercadopagoPaymentId) {
        this.mercadopagoPaymentId = mercadopagoPaymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public Double getNetReceivedAmount() {
        return netReceivedAmount;
    }

    public void setNetReceivedAmount(Double netReceivedAmount) {
        this.netReceivedAmount = netReceivedAmount;
    }

    public LocalDateTime getDateApproved() {
        return dateApproved;
    }

    public void setDateApproved(LocalDateTime dateApproved) {
        this.dateApproved = dateApproved;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
