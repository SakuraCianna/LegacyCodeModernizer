package com.enterprise.wms.controller;

import com.enterprise.wms.dto.InventoryDTO;
import com.enterprise.wms.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * Spring Boot 2.7 Legacy REST Controller using javax.servlet and javax.validation.
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<InventoryDTO>> listAll(HttpServletRequest request) {
        List<InventoryDTO> items = inventoryService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryDTO> getById(@PathVariable("id") Long id) {
        InventoryDTO item = inventoryService.getItemById(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<InventoryDTO> getBySku(@PathVariable("sku") String sku) {
        InventoryDTO item = inventoryService.getItemBySku(sku);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/alerts/low-stock")
    public ResponseEntity<List<InventoryDTO>> getLowStockAlerts(
            @RequestParam(value = "threshold", defaultValue = "10") Integer threshold) {
        List<InventoryDTO> alerts = inventoryService.getLowStockAlerts(threshold);
        return ResponseEntity.ok(alerts);
    }

    @PostMapping
    public ResponseEntity<InventoryDTO> createItem(@Valid @RequestBody InventoryDTO dto) {
        InventoryDTO created = inventoryService.createItem(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<InventoryDTO> adjustStock(
            @PathVariable("id") Long id,
            @RequestParam("delta") Integer delta) {
        InventoryDTO updated = inventoryService.updateStock(id, delta);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable("id") Long id) {
        inventoryService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
