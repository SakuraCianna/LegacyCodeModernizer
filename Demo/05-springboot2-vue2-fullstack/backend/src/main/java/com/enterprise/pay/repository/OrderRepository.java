package com.enterprise.pay.repository;

import com.enterprise.pay.constant.OrderStatus;
import com.enterprise.pay.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByOrderNo(String orderNo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OrderEntity o WHERE o.orderNo = :orderNo")
    Optional<OrderEntity> findByOrderNoForUpdate(@Param("orderNo") String orderNo);

    List<OrderEntity> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<OrderEntity> findByStatusOrderByCreatedAtDesc(OrderStatus status);
}
