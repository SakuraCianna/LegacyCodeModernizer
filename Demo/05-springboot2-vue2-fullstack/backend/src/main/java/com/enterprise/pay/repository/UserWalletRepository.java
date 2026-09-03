package com.enterprise.pay.repository;

import com.enterprise.pay.entity.UserWalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface UserWalletRepository extends JpaRepository<UserWalletEntity, Long> {

    Optional<UserWalletEntity> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM UserWalletEntity w WHERE w.userId = :userId")
    Optional<UserWalletEntity> findByUserIdForUpdate(@Param("userId") Long userId);

    // Atomic Balance Deduction with optimistic lock version increment & non-negative floor check
    @Modifying
    @Query("UPDATE UserWalletEntity w SET w.balance = w.balance - :amount, w.version = w.version + 1 WHERE w.userId = :userId AND w.version = :version AND w.balance >= :amount")
    int deductBalanceOptimistic(@Param("userId") Long userId, @Param("amount") BigDecimal amount, @Param("version") Integer version);

    @Modifying
    @Query("UPDATE UserWalletEntity w SET w.balance = w.balance + :amount, w.version = w.version + 1 WHERE w.userId = :userId")
    int creditBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}
