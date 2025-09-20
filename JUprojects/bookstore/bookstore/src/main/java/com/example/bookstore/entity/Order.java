package com.example.bookstore.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "`order`")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime orderDate;
    private Double totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 修改为使用 CascadeType.PERSIST 而不是 CascadeType.ALL
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 订单状态
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // 收货信息
    private String shippingAddress;
    private String recipientName;
    private String recipientPhone;

    // 支付信息
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private LocalDateTime paymentDate;

    public enum OrderStatus {
        pending, processing, completed, cancelled
    }

    public enum PaymentMethod {
        credit_card, debit_card, paypal, wechat, alipay, cash
    }

    public enum PaymentStatus {
        pending, paid, failed
    }

    // 添加辅助方法以确保双向关联正确设置
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }
}