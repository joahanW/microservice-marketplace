package com.learn.orderservice.controller;

import com.learn.orderservice.dto.OrderLineItemsDto;
import com.learn.orderservice.dto.OrderRequest;
import com.learn.orderservice.dto.OrderResponse;
import com.learn.orderservice.model.Order;
import com.learn.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getAll() {
        return orderService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        orderService.placeOrder(orderRequest);
        return "Order Placed Seccessfully";
    }


}
