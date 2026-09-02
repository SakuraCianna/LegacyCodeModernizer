package com.enterprise.wms.repository;

import com.enterprise.wms.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {

    Optional<InventoryItem> findBySkuCode(String skuCode);

    List<InventoryItem> findByCategoryIgnoreCase(String category);

    @Query("SELECT i FROM InventoryItem i WHERE i.quantity <= :threshold ORDER BY i.quantity ASC")
    List<InventoryItem> findLowStockItems(@Param("threshold") Integer threshold);
}
