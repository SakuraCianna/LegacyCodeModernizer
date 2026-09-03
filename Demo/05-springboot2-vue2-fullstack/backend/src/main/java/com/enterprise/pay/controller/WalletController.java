package com.enterprise.pay.controller;

import com.enterprise.pay.entity.FinancialAuditLogEntity;
import com.enterprise.pay.entity.UserWalletEntity;
import com.enterprise.pay.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserWalletEntity> getWallet(@PathVariable("userId") Long userId) {
        UserWalletEntity wallet = walletService.getOrCreateWallet(userId);
        return ResponseEntity.ok(wallet);
    }

    @PostMapping("/{userId}/deposit")
    public ResponseEntity<UserWalletEntity> deposit(
            @PathVariable("userId") Long userId,
            @RequestBody Map<String, Object> body) {
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        String remark = body.getOrDefault("remark", "Online Topup").toString();
        UserWalletEntity wallet = walletService.deposit(userId, amount, remark);
        return ResponseEntity.ok(wallet);
    }

    @GetMapping("/{userId}/audit-logs")
    public ResponseEntity<List<FinancialAuditLogEntity>> getAuditLogs(@PathVariable("userId") Long userId) {
        List<FinancialAuditLogEntity> logs = walletService.getAuditLogs(userId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/audit-logs/all")
    public ResponseEntity<List<FinancialAuditLogEntity>> getAllAuditLogs() {
        List<FinancialAuditLogEntity> logs = walletService.getAuditLogs(null);
        return ResponseEntity.ok(logs);
    }
}
