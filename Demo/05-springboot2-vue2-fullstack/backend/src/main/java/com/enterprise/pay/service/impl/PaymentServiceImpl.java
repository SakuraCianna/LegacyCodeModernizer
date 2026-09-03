package com.enterprise.pay.service.impl;

import com.enterprise.pay.constant.OrderStatus;
import com.enterprise.pay.constant.PaymentChannel;
import com.enterprise.pay.dto.OrderResponseDTO;
import com.enterprise.pay.dto.PaymentRequestDTO;
import com.enterprise.pay.entity.OrderEntity;
import com.enterprise.pay.entity.PaymentRecordEntity;
import com.enterprise.pay.repository.OrderRepository;
import com.enterprise.pay.repository.PaymentRecordRepository;
import com.enterprise.pay.service.PaymentService;
import com.enterprise.pay.service.RedisDistributedLockService;
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
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRecordRepository paymentRecordRepository;
    private final WalletService walletService;
    private final RedisDistributedLockService redisLockService;

    @Autowired
    public PaymentServiceImpl(OrderRepository orderRepository,
                              PaymentRecordRepository paymentRecordRepository,
                              WalletService walletService,
                              RedisDistributedLockService redisLockService) {
        this.orderRepository = orderRepository;
        this.paymentRecordRepository = paymentRecordRepository;
        this.walletService = walletService;
        this.redisLockService = redisLockService;
    }

    @Override
    public OrderEntity createOrder(Long userId, String title, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Order amount must be greater than zero");
        }
        OrderEntity order = new OrderEntity();
        order.setOrderNo("ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase());
        order.setUserId(userId);
        order.setTitle(title);
        order.setTotalAmount(amount);
        order.setRefundedAmount(BigDecimal.ZERO);
        order.setStatus(OrderStatus.CREATED);
        return orderRepository.save(order);
    }

    @Override
    public OrderResponseDTO payOrder(PaymentRequestDTO request) {
        String lockKey = "lock:pay:order:" + request.getOrderNo();
        String requestId = UUID.randomUUID().toString();

        // 1. Rate Limiting Check (5 req/sec per user)
        if (!redisLockService.checkRateLimit(String.valueOf(request.getUserId()), 5, 1000)) {
            throw new IllegalStateException("Payment rate limit exceeded. Please wait a moment.");
        }

        // 2. Concurrency Control: Acquire Redis Distributed Lock
        boolean locked = redisLockService.tryAcquireLock(lockKey, requestId, 5000);
        if (!locked) {
            throw new IllegalStateException("Concurrent payment in progress for order " + request.getOrderNo());
        }

        try {
            // 3. Idempotency Check: Prevent duplicate payment charging
            if (paymentRecordRepository.findByIdempotencyToken(request.getIdempotencyToken()).isPresent()) {
                throw new IllegalStateException("Duplicate payment request detected for token: " + request.getIdempotencyToken());
            }

            // 4. Query Order with Pessimistic Lock
            OrderEntity order = orderRepository.findByOrderNoForUpdate(request.getOrderNo())
                    .orElseThrow(() -> new IllegalArgumentException("Order not found: " + request.getOrderNo()));

            if (order.getStatus() != OrderStatus.CREATED) {
                throw new IllegalStateException("Order status is not CREATED. Current status: " + order.getStatus());
            }

            if (order.getTotalAmount().compareTo(request.getAmount()) != 0) {
                throw new IllegalArgumentException("Payment amount mismatch. Expected: " + order.getTotalAmount() + ", Got: " + request.getAmount());
            }

            // 5. Deduct Balance if Channel is WALLET_BALANCE
            if (request.getChannel() == PaymentChannel.WALLET_BALANCE) {
                walletService.deductBalanceOptimistic(request.getUserId(), request.getAmount(), order.getOrderNo());
            }

            // 6. Record Payment Transaction
            PaymentRecordEntity record = new PaymentRecordEntity();
            record.setSerialNo("PAY-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
            record.setOrderNo(order.getOrderNo());
            record.setUserId(request.getUserId());
            record.setAmount(request.getAmount());
            record.setChannel(request.getChannel());
            record.setIdempotencyToken(request.getIdempotencyToken());
            record.setSignature("SIG-MOCK-HASH-" + record.getSerialNo());
            paymentRecordRepository.save(record);

            // 7. Update Order State Machine
            order.setStatus(OrderStatus.SUCCESS);
            order.setPaidAt(new Date());
            OrderEntity savedOrder = orderRepository.save(order);

            return toDTO(savedOrder);
        } finally {
            // Release Distributed Lock safely via Lua script
            redisLockService.releaseLock(lockKey, requestId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrderEntity getOrderByNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderNo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderEntity> listOrdersByUser(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentRecordEntity> listPaymentRecords(String orderNo) {
        return paymentRecordRepository.findByOrderNoOrderByCreatedAtDesc(orderNo);
    }

    private OrderResponseDTO toDTO(OrderEntity o) {
        return new OrderResponseDTO(
                o.getId(),
                o.getOrderNo(),
                o.getUserId(),
                o.getTitle(),
                o.getTotalAmount(),
                o.getRefundedAmount(),
                o.getStatus(),
                o.getCreatedAt(),
                o.getPaidAt()
        );
    }
}
