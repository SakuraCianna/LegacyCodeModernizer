package com.enterprise.pay.service;

import com.enterprise.pay.dto.OrderResponseDTO;
import com.enterprise.pay.dto.PaymentRequestDTO;
import com.enterprise.pay.entity.OrderEntity;
import com.enterprise.pay.entity.PaymentRecordEntity;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {

    OrderEntity createOrder(Long userId, String title, BigDecimal amount);

    OrderResponseDTO payOrder(PaymentRequestDTO request);

    OrderEntity getOrderByNo(String orderNo);

    List<OrderEntity> listOrdersByUser(Long userId);

    List<PaymentRecordEntity> listPaymentRecords(String orderNo);
}
