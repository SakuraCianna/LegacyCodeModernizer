package com.enterprise.pay.service.impl;

import com.enterprise.pay.constant.OrderStatus;
import com.enterprise.pay.constant.RefundStatus;
import com.enterprise.pay.dto.RefundRequestDTO;
import com.enterprise.pay.entity.OrderEntity;
import com.enterprise.pay.entity.RefundRecordEntity;
import com.enterprise.pay.repository.OrderRepository;
import com.enterprise.pay.repository.RefundRecordRepository;
import com.enterprise.pay.service.RedisDistributedLockService;
import com.enterprise.pay.service.RefundService;
import com.enterprise.pay.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class RefundServiceImpl implements RefundService {

    private final OrderRepository orderRepository;
    private final RefundRecordRepository refundRecordRepository;
    private final WalletService walletService;
    private final RedisDistributedLockService redisLockService;

    @Autowired
    public RefundServiceImpl(OrderRepository orderRepository,
                             RefundRecordRepository refundRecordRepository,
                             WalletService walletService,
                             RedisDistributedLockService redisLockService) {
        this.orderRepository = orderRepository;
        this.refundRecordRepository = refundRecordRepository;
        this.walletService = walletService;
        this.redisLockService = redisLockService;
    }

    @Override
    public RefundRecordEntity applyRefund(RefundRequestDTO request) {
        String lockKey = "lock:refund:order:" + request.getOrderNo();
        String requestId = UUID.randomUUID().toString();

        boolean locked = redisLockService.tryAcquireLock(lockKey, requestId, 5000);
        if (!locked) {
            throw new IllegalStateException("Another refund request is currently being processed for order: " + request.getOrderNo());
        }

        try {
            OrderEntity order = orderRepository.findByOrderNoForUpdate(request.getOrderNo())
                    .orElseThrow(() -> new IllegalArgumentException("Order not found: " + request.getOrderNo()));

            if (order.getStatus() != OrderStatus.SUCCESS && order.getStatus() != OrderStatus.REFUND_PARTIAL) {
                throw new IllegalStateException("Only PAID or PARTIAL_REFUNDED orders can be refunded. Current status: " + order.getStatus());
            }

            // Fool-proof max refundable balance boundary check
            BigDecimal maxRefundable = order.getTotalAmount().subtract(order.getRefundedAmount());
            if (request.getRefundAmount().compareTo(maxRefundable) > 0) {
                throw new IllegalArgumentException("Refund amount $" + request.getRefundAmount() + " exceeds max refundable balance $" + maxRefundable);
            }

            RefundRecordEntity refund = new RefundRecordEntity();
            refund.setRefundNo("REF-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
            refund.setOrderNo(order.getOrderNo());
            refund.setUserId(request.getUserId());
            refund.setRefundAmount(request.getRefundAmount());
            refund.setReason(request.getReason());
            refund.setStatus(RefundStatus.PENDING);
            refund.setAuditBy(request.getAuditBy());

            return refundRecordRepository.save(refund);
        } finally {
            redisLockService.releaseLock(lockKey, requestId);
        }
    }

    @Override
    public RefundRecordEntity auditRefund(String refundNo, boolean approved, String auditor) {
        RefundRecordEntity refund = refundRecordRepository.findByRefundNo(refundNo)
                .orElseThrow(() -> new IllegalArgumentException("Refund record not found: " + refundNo));

        if (refund.getStatus() != RefundStatus.PENDING) {
            throw new IllegalStateException("Refund request has already been processed: " + refund.getStatus());
        }

        if (!approved) {
            refund.setStatus(RefundStatus.REJECTED);
            refund.setAuditBy(auditor);
            refund.setCompletedAt(new Date());
            return refundRecordRepository.save(refund);
        }

        OrderEntity order = orderRepository.findByOrderNoForUpdate(refund.getOrderNo())
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + refund.getOrderNo()));

        // Credit Wallet Balance
        walletService.refundCredit(refund.getUserId(), refund.getRefundAmount(), refund.getRefundNo());

        // Update Order refunded amount and status
        BigDecimal newRefunded = order.getRefundedAmount().add(refund.getRefundAmount());
        order.setRefundedAmount(newRefunded);

        if (newRefunded.compareTo(order.getTotalAmount()) >= 0) {
            order.setStatus(OrderStatus.REFUND_FULL);
        } else {
            order.setStatus(OrderStatus.REFUND_PARTIAL);
        }
        orderRepository.save(order);

        // Update Refund record status
        refund.setStatus(RefundStatus.SUCCESS);
        refund.setAuditBy(auditor);
        refund.setCompletedAt(new Date());
        return refundRecordRepository.save(refund);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefundRecordEntity> listRefundsByOrder(String orderNo) {
        return refundRecordRepository.findByOrderNoOrderByCreatedAtDesc(orderNo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefundRecordEntity> listAllRefunds() {
        return refundRecordRepository.findAll();
    }
}
