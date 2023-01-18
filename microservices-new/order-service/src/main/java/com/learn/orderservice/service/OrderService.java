package com.learn.orderservice.service;

import com.learn.orderservice.dto.InventoryResponse;
import com.learn.orderservice.dto.OrderLineItemsDto;
import com.learn.orderservice.dto.OrderRequest;
import com.learn.orderservice.dto.OrderResponse;
import com.learn.orderservice.model.Order;
import com.learn.orderservice.model.OrderLineItem;
import com.learn.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final WebClient.Builder webClientBuilder;

    public List<OrderResponse> getAll() {
        return orderRepository.findAll()
                .stream().map(order -> modelMapper.map(order, OrderResponse.class))
                .collect(Collectors.toList());
    }

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItemsDto()
                .stream().map(this::mapToOrderLineItem)
                .collect(Collectors.toList());
        order.setOrderLineItems(orderLineItems);

        // Get All SKU-CODE
        List<String> skuCodes = order.getOrderLineItems().stream()
                .map(OrderLineItem::getSkuCode)
                .collect(Collectors.toList());

        // Call Inventory Service and place order if product is in stock
        // Method block() make the request Sync
        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        // Validation in Array using allMatch if all value is TRUE,
        // If one of the value FALSE the var return FALSE
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
                .allMatch(InventoryResponse::isInStock);

        if (allProductsInStock) {
            orderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again!");
        }
    }

    private OrderLineItem mapToOrderLineItem(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItem.setPrice(orderLineItemsDto.getPrice());
        orderLineItem.setQuantity(orderLineItemsDto.getQuantity());
        return orderLineItem;
    }

}
