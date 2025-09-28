// Order.java - 添加缺失的注解和字段
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

    @ManyToOne(fetch = FetchType.EAGER) // 改为EAGER避免延迟加载问题
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    // 订单状态
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.pending;

    // 收货信息
    private String shippingAddress;
    private String recipientName;
    private String recipientPhone;

    // 支付信息
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.pending;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum OrderStatus {
        pending, processing, completed, cancelled
    }

    public enum PaymentMethod {
        credit_card, debit_card, paypal, wechat, alipay, cash
    }

    public enum PaymentStatus {
        pending, paid, failed, refunded
    }

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 辅助方法
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }
}