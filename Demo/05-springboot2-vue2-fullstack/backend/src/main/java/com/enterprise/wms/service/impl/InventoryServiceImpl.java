package com.enterprise.wms.service.impl;

import com.enterprise.wms.dto.InventoryDTO;
import com.enterprise.wms.entity.InventoryItem;
import com.enterprise.wms.repository.InventoryRepository;
import com.enterprise.wms.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDTO> getAllItems() {
        return inventoryRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryDTO getItemById(Long id) {
        return inventoryRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryDTO getItemBySku(String skuCode) {
        return inventoryRepository.findBySkuCode(skuCode)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Item not found with SKU: " + skuCode));
    }

    @Override
    public InventoryDTO createItem(InventoryDTO dto) {
        if (inventoryRepository.findBySkuCode(dto.getSkuCode()).isPresent()) {
            throw new IllegalArgumentException("SKU already exists: " + dto.getSkuCode());
        }
        InventoryItem entity = new InventoryItem();
        entity.setSkuCode(dto.getSkuCode());
        entity.setItemName(dto.getItemName());
        entity.setCategory(dto.getCategory());
        entity.setQuantity(dto.getQuantity());
        entity.setUnitPrice(dto.getUnitPrice());
        entity.setLocationZone(dto.getLocationZone());

        InventoryItem saved = inventoryRepository.save(entity);
        return toDTO(saved);
    }

    @Override
    public InventoryDTO updateStock(Long id, Integer quantityDelta) {
        InventoryItem item = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with id: " + id));
        
        int updatedQty = item.getQuantity() + quantityDelta;
        if (updatedQty < 0) {
            throw new IllegalArgumentException("Insufficient inventory to reduce by " + Math.abs(quantityDelta));
        }
        item.setQuantity(updatedQty);
        return toDTO(inventoryRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDTO> getLowStockAlerts(Integer threshold) {
        int limit = (threshold != null) ? threshold : 10;
        return inventoryRepository.findLowStockItems(limit).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete non-existent item: " + id);
        }
        inventoryRepository.deleteById(id);
    }

    private InventoryDTO toDTO(InventoryItem entity) {
        return new InventoryDTO(
                entity.getId(),
                entity.getSkuCode(),
                entity.getItemName(),
                entity.getCategory(),
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getLocationZone()
        );
    }
}
