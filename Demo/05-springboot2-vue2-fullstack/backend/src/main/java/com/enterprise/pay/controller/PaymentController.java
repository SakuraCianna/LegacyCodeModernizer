package com.enterprise.pay.controller;

import com.enterprise.pay.dto.OrderResponseDTO;
import com.enterprise.pay.dto.PaymentRequestDTO;
import com.enterprise.pay.entity.OrderEntity;
import com.enterprise.pay.entity.PaymentRecordEntity;
import com.enterprise.pay.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/order/create")
    public ResponseEntity<OrderEntity> createOrder(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String title = (String) body.get("title");
        BigDecimal amount = new BigDecimal(body.get("amount").toString());

        OrderEntity order = paymentService.createOrder(userId, title, amount);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PostMapping("/pay")
    public ResponseEntity<OrderResponseDTO> processPayment(@Valid @RequestBody PaymentRequestDTO request) {
        OrderResponseDTO response = paymentService.payOrder(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/order/{orderNo}")
    public ResponseEntity<OrderEntity> getOrder(@PathVariable("orderNo") String orderNo) {
        OrderEntity order = paymentService.getOrderByNo(orderNo);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/user/{userId}/orders")
    public ResponseEntity<List<OrderEntity>> listUserOrders(@PathVariable("userId") Long userId) {
        List<OrderEntity> orders = paymentService.listOrdersByUser(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/records/{orderNo}")
    public ResponseEntity<List<PaymentRecordEntity>> listRecords(@PathVariable("orderNo") String orderNo) {
        List<PaymentRecordEntity> records = paymentService.listPaymentRecords(orderNo);
        return ResponseEntity.ok(records);
    }
}
