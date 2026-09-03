package com.enterprise.pay.service;

import com.enterprise.pay.entity.FinancialAuditLogEntity;
import com.enterprise.pay.entity.UserWalletEntity;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {

    UserWalletEntity getOrCreateWallet(Long userId);

    UserWalletEntity deposit(Long userId, BigDecimal amount, String remark);

    boolean deductBalanceOptimistic(Long userId, BigDecimal amount, String bizRef);

    boolean refundCredit(Long userId, BigDecimal amount, String bizRef);

    List<FinancialAuditLogEntity> getAuditLogs(Long userId);
}
