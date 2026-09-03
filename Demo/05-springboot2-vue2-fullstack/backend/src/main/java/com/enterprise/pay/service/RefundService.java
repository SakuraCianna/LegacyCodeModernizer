package com.enterprise.pay.service;

import com.enterprise.pay.dto.RefundRequestDTO;
import com.enterprise.pay.entity.RefundRecordEntity;

import java.util.List;

public interface RefundService {

    RefundRecordEntity applyRefund(RefundRequestDTO request);

    RefundRecordEntity auditRefund(String refundNo, boolean approved, String auditor);

    List<RefundRecordEntity> listRefundsByOrder(String orderNo);

    List<RefundRecordEntity> listAllRefunds();
}
