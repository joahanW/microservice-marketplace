package com.learn.inventoryservice.controller;

import com.learn.inventoryservice.dto.InventoryResponse;
import com.learn.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // Using PathVariable (only handle 1 Items)
    // http://localhost:8090/api/inventory/iphone_13

    // Using PathPharam (handle moren than 2 items)
    // http://localhost:8090/api/inventory?sku-code=iphone-13&sku-code=iphone13-red......
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInstock(@RequestParam List<String> skuCode){
        return inventoryService.isInStock(skuCode);
    }

}
