package com.enterprise.pay.repository;

import com.enterprise.pay.entity.PaymentRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecordEntity, Long> {

    Optional<PaymentRecordEntity> findBySerialNo(String serialNo);

    Optional<PaymentRecordEntity> findByIdempotencyToken(String idempotencyToken);

    List<PaymentRecordEntity> findByOrderNoOrderByCreatedAtDesc(String orderNo);

    List<PaymentRecordEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
}
