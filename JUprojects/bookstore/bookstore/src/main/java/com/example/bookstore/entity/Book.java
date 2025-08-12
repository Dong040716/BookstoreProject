package com.example.bookstore.entity;

import jakarta.persistence.*;
import lombok.Data;

// @Entity 表示这是一个 JPA 实体类（对应数据库中的一张表）
// 类名默认映射为表名（Book -> book）
@Entity
@Table(name = "book")
@Data
public class Book {
    @Id  // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增主键
    private Long id;

    private String title; // 书名
    private String author;// 作者
    private Double price; // 价格
    private Integer stock;// 库存数量
    private String category;// 目录
}
