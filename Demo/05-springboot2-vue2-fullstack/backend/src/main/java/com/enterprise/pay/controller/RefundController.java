package com.enterprise.pay.controller;

import com.enterprise.pay.dto.RefundRequestDTO;
import com.enterprise.pay.entity.RefundRecordEntity;
import com.enterprise.pay.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/refund")
public class RefundController {

    private final RefundService refundService;

    @Autowired
    public RefundController(RefundService refundService) {
        this.refundService = refundService;
    }

    @PostMapping("/apply")
    public ResponseEntity<RefundRecordEntity> applyRefund(@Valid @RequestBody RefundRequestDTO request) {
        RefundRecordEntity record = refundService.applyRefund(request);
        return new ResponseEntity<>(record, HttpStatus.CREATED);
    }

    @PostMapping("/audit/{refundNo}")
    public ResponseEntity<RefundRecordEntity> auditRefund(
            @PathVariable("refundNo") String refundNo,
            @RequestBody Map<String, Object> body) {
        boolean approved = Boolean.parseBoolean(body.getOrDefault("approved", "true").toString());
        String auditor = body.getOrDefault("auditor", "Admin").toString();

        RefundRecordEntity audited = refundService.auditRefund(refundNo, approved, auditor);
        return ResponseEntity.ok(audited);
    }

    @GetMapping("/order/{orderNo}")
    public ResponseEntity<List<RefundRecordEntity>> listByOrder(@PathVariable("orderNo") String orderNo) {
        List<RefundRecordEntity> list = refundService.listRefundsByOrder(orderNo);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/list")
    public ResponseEntity<List<RefundRecordEntity>> listAll() {
        List<RefundRecordEntity> list = refundService.listAllRefunds();
        return ResponseEntity.ok(list);
    }
}
