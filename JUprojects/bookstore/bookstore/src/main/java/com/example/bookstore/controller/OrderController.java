package com.example.bookstore.controller;

import com.example.bookstore.entity.Order;
import com.example.bookstore.repository.OrderRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public List<Order> listOrders() {
        return orderRepository.findAll();
    }

    @PostMapping
    public Order addOrder(@RequestBody Order order) {
        return orderRepository.save(order);
    }


}
