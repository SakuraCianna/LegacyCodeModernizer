package com.enterprise.pay;

import com.enterprise.pay.constant.OrderStatus;
import com.enterprise.pay.constant.PaymentChannel;
import com.enterprise.pay.constant.RefundStatus;
import com.enterprise.pay.dto.OrderResponseDTO;
import com.enterprise.pay.dto.PaymentRequestDTO;
import com.enterprise.pay.dto.RefundRequestDTO;
import com.enterprise.pay.entity.OrderEntity;
import com.enterprise.pay.entity.RefundRecordEntity;
import com.enterprise.pay.entity.UserWalletEntity;
import com.enterprise.pay.service.PaymentService;
import com.enterprise.pay.service.RefundService;
import com.enterprise.pay.service.WalletService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest
public class PaymentServiceIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RefundService refundService;

    @Autowired
    private WalletService walletService;

    private Long testUserId = 8899L;

    @BeforeEach
    public void setup() {
        walletService.getOrCreateWallet(testUserId);
        walletService.deposit(testUserId, new BigDecimal("500.00"), "Test Initial Balance");
    }

    @Test
    public void testCompletePaymentAndRefundLifecycle() {
        // 1. Create Order
        BigDecimal orderAmount = new BigDecimal("150.00");
        OrderEntity order = paymentService.createOrder(testUserId, "MacBook Pro Accessory Kit", orderAmount);
        Assertions.assertNotNull(order.getId());
        Assertions.assertEquals(OrderStatus.CREATED, order.getStatus());

        // 2. Pay Order via Wallet Balance
        PaymentRequestDTO payReq = new PaymentRequestDTO();
        payReq.setOrderNo(order.getOrderNo());
        payReq.setUserId(testUserId);
        payReq.setAmount(orderAmount);
        payReq.setChannel(PaymentChannel.WALLET_BALANCE);
        payReq.setIdempotencyToken("IDEM-" + UUID.randomUUID().toString());

        OrderResponseDTO paidOrder = paymentService.payOrder(payReq);
        Assertions.assertEquals(OrderStatus.SUCCESS, paidOrder.getStatus());
        Assertions.assertNotNull(paidOrder.getPaidAt());

        // Verify Wallet balance deduction (1000 + 500 - 150 = 1350)
        UserWalletEntity wallet = walletService.getOrCreateWallet(testUserId);
        Assertions.assertTrue(wallet.getBalance().compareTo(new BigDecimal("1350.00")) == 0);

        // 3. Test Idempotency Guard (Duplicate payment must fail)
        Assertions.assertThrows(IllegalStateException.class, () -> {
            paymentService.payOrder(payReq);
        });

        // 4. Apply Partial Refund ($50.00)
        RefundRequestDTO partialReq = new RefundRequestDTO();
        partialReq.setOrderNo(order.getOrderNo());
        partialReq.setUserId(testUserId);
        partialReq.setRefundAmount(new BigDecimal("50.00"));
        partialReq.setReason("Item returned due to minor scratch");
        partialReq.setAuditBy("FinanceAuditor01");

        RefundRecordEntity partialRefund = refundService.applyRefund(partialReq);
        Assertions.assertEquals(RefundStatus.PENDING, partialRefund.getStatus());

        // Audit & Approve Partial Refund
        RefundRecordEntity approvedPartial = refundService.auditRefund(partialRefund.getRefundNo(), true, "SeniorAuditor");
        Assertions.assertEquals(RefundStatus.SUCCESS, approvedPartial.getStatus());

        OrderEntity orderAfterPartial = paymentService.getOrderByNo(order.getOrderNo());
        Assertions.assertEquals(OrderStatus.REFUND_PARTIAL, orderAfterPartial.getStatus());
        Assertions.assertEquals(new BigDecimal("50.00"), orderAfterPartial.getRefundedAmount());

        // 5. Apply Remaining Full Refund ($100.00)
        RefundRequestDTO fullReq = new RefundRequestDTO();
        fullReq.setOrderNo(order.getOrderNo());
        fullReq.setUserId(testUserId);
        fullReq.setRefundAmount(new BigDecimal("100.00"));
        fullReq.setReason("Remaining accessories returned");
        fullReq.setAuditBy("FinanceAuditor01");

        RefundRecordEntity fullRefund = refundService.applyRefund(fullReq);
        refundService.auditRefund(fullRefund.getRefundNo(), true, "SeniorAuditor");

        OrderEntity orderAfterFull = paymentService.getOrderByNo(order.getOrderNo());
        Assertions.assertEquals(OrderStatus.REFUND_FULL, orderAfterFull.getStatus());
        Assertions.assertEquals(new BigDecimal("150.00"), orderAfterFull.getRefundedAmount());

        // 6. Test Exceeding Refund Guard (Must fail)
        RefundRequestDTO exceedReq = new RefundRequestDTO();
        exceedReq.setOrderNo(order.getOrderNo());
        exceedReq.setUserId(testUserId);
        exceedReq.setRefundAmount(new BigDecimal("10.00"));
        exceedReq.setReason("Extra claim");
        exceedReq.setAuditBy("FinanceAuditor01");

        Assertions.assertThrows(IllegalStateException.class, () -> {
            refundService.applyRefund(exceedReq);
        });
    }
}
