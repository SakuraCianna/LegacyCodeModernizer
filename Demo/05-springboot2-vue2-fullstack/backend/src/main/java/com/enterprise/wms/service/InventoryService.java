package com.enterprise.wms.service;

import com.enterprise.wms.dto.InventoryDTO;
import java.util.List;

public interface InventoryService {

    List<InventoryDTO> getAllItems();

    InventoryDTO getItemById(Long id);

    InventoryDTO getItemBySku(String skuCode);

    InventoryDTO createItem(InventoryDTO dto);

    InventoryDTO updateStock(Long id, Integer quantityDelta);

    List<InventoryDTO> getLowStockAlerts(Integer threshold);

    void deleteItem(Long id);
}
