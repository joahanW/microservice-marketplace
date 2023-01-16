package com.learn.orderservice.service;

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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final WebClient webClient;

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

        // Call Inventory Service and place order if product is in stock
        Boolean result = webClient.get().uri("http://localhost:8090/api/inventory")
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        if (result) {
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
