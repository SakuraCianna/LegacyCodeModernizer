package com.enterprise.pay.service.impl;

import com.enterprise.pay.entity.FinancialAuditLogEntity;
import com.enterprise.pay.entity.UserWalletEntity;
import com.enterprise.pay.repository.FinancialAuditLogRepository;
import com.enterprise.pay.repository.UserWalletRepository;
import com.enterprise.pay.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private final UserWalletRepository walletRepository;
    private final FinancialAuditLogRepository auditLogRepository;

    @Autowired
    public WalletServiceImpl(UserWalletRepository walletRepository, FinancialAuditLogRepository auditLogRepository) {
        this.walletRepository = walletRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public UserWalletEntity getOrCreateWallet(Long userId) {
        return walletRepository.findByUserId(userId).orElseGet(() -> {
            UserWalletEntity w = new UserWalletEntity();
            w.setUserId(userId);
            w.setBalance(new BigDecimal("1000.00")); // Initial seed balance for demo testing
            w.setFrozenBalance(BigDecimal.ZERO);
            return walletRepository.save(w);
        });
    }

    @Override
    public UserWalletEntity deposit(Long userId, BigDecimal amount, String remark) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        UserWalletEntity wallet = getOrCreateWallet(userId);
        BigDecimal before = wallet.getBalance();
        wallet.setBalance(before.add(amount));
        UserWalletEntity saved = walletRepository.save(wallet);

        recordAudit(userId, "DEPOSIT", "DEP-" + System.currentTimeMillis(), before, amount, saved.getBalance(), remark);
        return saved;
    }

    @Override
    public boolean deductBalanceOptimistic(Long userId, BigDecimal amount, String bizRef) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deduct amount must be positive");
        }
        UserWalletEntity wallet = getOrCreateWallet(userId);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance. Current: $" + wallet.getBalance() + ", Required: $" + amount);
        }

        BigDecimal before = wallet.getBalance();
        int rows = walletRepository.deductBalanceOptimistic(userId, amount, wallet.getVersion());
        if (rows == 0) {
            throw new IllegalStateException("Concurrent modification detected on wallet balance for user " + userId + ". Please retry.");
        }

        BigDecimal after = before.subtract(amount);
        recordAudit(userId, "ORDER_PAY", bizRef, before, amount.negate(), after, "Wallet balance payment deduction");
        return true;
    }

    @Override
    public boolean refundCredit(Long userId, BigDecimal amount, String bizRef) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Refund credit amount must be positive");
        }
        UserWalletEntity wallet = getOrCreateWallet(userId);
        BigDecimal before = wallet.getBalance();
        walletRepository.creditBalance(userId, amount);

        BigDecimal after = before.add(amount);
        recordAudit(userId, "REFUND_CREDIT", bizRef, before, amount, after, "Order refund balance credit");
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FinancialAuditLogEntity> getAuditLogs(Long userId) {
        if (userId != null) {
            return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
        }
        return auditLogRepository.findTop50ByOrderByCreatedAtDesc();
    }

    private void recordAudit(Long userId, String bizType, String refNo, BigDecimal before, BigDecimal delta, BigDecimal after, String remark) {
        FinancialAuditLogEntity log = new FinancialAuditLogEntity();
        log.setUserId(userId);
        log.setBizType(bizType);
        log.setBizReferenceNo(refNo);
        log.setBeforeBalance(before);
        log.setDeltaAmount(delta);
        log.setAfterBalance(after);
        log.setRemark(remark);
        auditLogRepository.save(log);
    }
}
