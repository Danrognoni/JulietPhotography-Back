package com.julietamarateo.photography.repository;

import com.julietamarateo.photography.entity.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, Long> {

    Optional<PaymentRecord> findByMercadopagoPaymentId(String mercadopagoPaymentId);

    List<PaymentRecord> findByOrderIdOrderByCreatedAtDesc(String orderId);
}
