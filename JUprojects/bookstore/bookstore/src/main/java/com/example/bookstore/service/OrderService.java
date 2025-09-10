package com.example.bookstore.service;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Order;
import com.example.bookstore.entity.OrderItem;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.OrderItemRepository;
import com.example.bookstore.repository.OrderRepository;
import com.example.bookstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
     * 创建订单 - 包含完整的业务逻辑
     * 1. 验证用户存在
     * 2. 验证每个订单项的图书存在且有足够库存
     * 3. 计算订单总价
     * 4. 减少图书库存
     * 5. 保存订单和订单项
     */
    @Transactional
    public Order createOrder(Order order) {
        // 1. 验证用户存在
        User user = userRepository.findById(order.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("用户不存在，ID: " + order.getUser().getId()));

        // 设置订单基本信息
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalPrice(0.0);

        // 2. 处理每个订单项
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

            // 设置订单项信息
            item.setOrder(order);
            item.setBook(book);

            // 计算该项小计并累加到订单总价
            double itemTotal = book.getPrice() * item.getQuantity();
            order.setTotalPrice(order.getTotalPrice() + itemTotal);

            // 减少图书库存
            book.setStock(book.getStock() - item.getQuantity());
            bookRepository.save(book);

            // 保存订单项
            orderItemRepository.save(item);
        }

        // 3. 保存订单
        Order savedOrder = orderRepository.save(order);

        return savedOrder;
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
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