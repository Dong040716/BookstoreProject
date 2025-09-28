package com.example.bookstore.service;

import com.example.bookstore.entity.*;
import com.example.bookstore.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        BookRepository bookRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * 创建订单 - 重新设计流程
     */
    @Transactional
    public Order createOrder(Order order) {
        // 1. 验证用户存在
        User user = userRepository.findById(order.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在，ID: " + order.getUser().getId()));

        // 2. 创建新订单对象，避免使用传入的order（可能有问题）
        Order newOrder = new Order();
        newOrder.setUser(user);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setTotalPrice(0.0);
        newOrder.setStatus(Order.OrderStatus.pending);
        newOrder.setPaymentStatus(Order.PaymentStatus.pending);

        // 设置收货信息和支付方式
        newOrder.setShippingAddress(order.getShippingAddress() != null ? order.getShippingAddress() : "");
        newOrder.setRecipientName(order.getRecipientName() != null ? order.getRecipientName() : "");
        newOrder.setRecipientPhone(order.getRecipientPhone() != null ? order.getRecipientPhone() : "");
        newOrder.setPaymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod() : Order.PaymentMethod.cash);

        // 3. 先保存订单（生成ID）
        Order savedOrder = orderRepository.save(newOrder);

        // 确保订单ID已经生成
        if (savedOrder.getId() == null) {
            throw new IllegalStateException("订单ID生成失败");
        }

        // 4. 处理每个订单项
        List<OrderItem> savedOrderItems = new ArrayList<>();
        double totalPrice = 0.0;

        for (OrderItem item : order.getOrderItems()) {
            // 验证图书存在
            Book book = bookRepository.findById(item.getBook().getId())
                    .orElseThrow(() -> new IllegalArgumentException("图书不存在，ID: " + item.getBook().getId()));

            // 验证库存足够
            if (book.getStock() < item.getQuantity()) {
                throw new IllegalArgumentException("图书库存不足: " + book.getTitle() +
                        "，当前库存: " + book.getStock() +
                        "，请求数量: " + item.getQuantity());
            }

            // 创建新的订单项并设置关联
            OrderItem newItem = new OrderItem();
            newItem.setOrder(savedOrder);  // 使用已保存的订单
            newItem.setBook(book);
            newItem.setQuantity(item.getQuantity());
            newItem.setUnitPrice(book.getPrice());
            newItem.setSubtotal(book.getPrice() * item.getQuantity());

            // 计算该项小计并累加到订单总价
            double itemTotal = book.getPrice() * item.getQuantity();
            totalPrice += itemTotal;

            // 减少图书库存
            book.setStock(book.getStock() - item.getQuantity());
            bookRepository.save(book);

            // 保存订单项
            OrderItem savedItem = orderItemRepository.save(newItem);
            savedOrderItems.add(savedItem);
        }

        // 5. 设置订单总价，更新订单
        savedOrder.setTotalPrice(totalPrice);
        savedOrder.setOrderItems(savedOrderItems);
        return orderRepository.save(savedOrder);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    // 在OrderService类中添加这个方法
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public void deleteOrder(Long id) {
        // 先删除关联的订单项
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("订单不存在，ID: " + id));

        orderItemRepository.deleteAll(order.getOrderItems());

        // 再删除订单
        orderRepository.deleteById(id);
    }
}