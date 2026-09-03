package com.enterprise.pay.repository;

import com.enterprise.pay.constant.RefundStatus;
import com.enterprise.pay.entity.RefundRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefundRecordRepository extends JpaRepository<RefundRecordEntity, Long> {

    Optional<RefundRecordEntity> findByRefundNo(String refundNo);

    List<RefundRecordEntity> findByOrderNoOrderByCreatedAtDesc(String orderNo);

    List<RefundRecordEntity> findByStatusOrderByCreatedAtDesc(RefundStatus status);
}
