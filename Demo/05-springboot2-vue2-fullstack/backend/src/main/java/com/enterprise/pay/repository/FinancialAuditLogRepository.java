package com.enterprise.pay.repository;

import com.enterprise.pay.entity.FinancialAuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialAuditLogRepository extends JpaRepository<FinancialAuditLogEntity, Long> {

    List<FinancialAuditLogEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<FinancialAuditLogEntity> findByBizReferenceNo(String bizReferenceNo);

    List<FinancialAuditLogEntity> findTop50ByOrderByCreatedAtDesc();
}
