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
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    public List<OrderResponse> getAll(){
        return orderRepository.findAll()
                .stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .collect(Collectors.toList());
    }

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItem> orderLineItems = orderRequest.getOrderLineItemsDto()
                .stream()
                .map(this::mapToOrderLineItem)
                .collect(Collectors.toList());
        order.setOrderLineItems(orderLineItems);
        orderRepository.save(order);
    }

    private OrderLineItem mapToOrderLineItem(OrderLineItemsDto orderLineItemsDto){
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setSkuCode(orderLineItemsDto.getSkuCode());
        orderLineItem.setPrice(orderLineItemsDto.getPrice());
        orderLineItem.setQuantity(orderLineItemsDto.getQuantity());
        return orderLineItem;
    }

}
