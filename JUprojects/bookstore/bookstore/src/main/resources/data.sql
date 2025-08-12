
-- 创建数据库
CREATE DATABASE IF NOT EXISTS bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookstore;

-- 用户表（管理员、顾客）
CREATE TABLE user (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(50) NOT NULL UNIQUE,
                      password VARCHAR(100) NOT NULL,
                      role ENUM('admin', 'customer') NOT NULL DEFAULT 'customer'
) ENGINE=InnoDB;

-- 图书表
CREATE TABLE book (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      title VARCHAR(100) NOT NULL,
                      author VARCHAR(100) NOT NULL,
                      price DECIMAL(10,2) NOT NULL,
                      stock INT NOT NULL DEFAULT 0,
                      category VARCHAR(50)
) ENGINE=InnoDB;

-- 订单表
CREATE TABLE `order` (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         user_id BIGINT NOT NULL,
                         order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         total_price DECIMAL(10,2) NOT NULL,
                         CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 订单项表
CREATE TABLE order_item (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            order_id BIGINT NOT NULL,
                            book_id BIGINT NOT NULL,
                            quantity INT NOT NULL,
                            CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES `order`(id) ON DELETE CASCADE,
                            CONSTRAINT fk_orderitem_book FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 插入一些测试数据
INSERT INTO user (username, password, role) VALUES
                                                ('admin', '123456', 'admin'),
                                                ('alice', '123456', 'customer'),
                                                ('bob', '123456', 'customer');

INSERT INTO book (title, author, price, stock, category) VALUES
                                                             ('Java编程思想', 'Bruce Eckel', 88.50, 10, '编程'),
                                                             ('Spring Boot实战', 'Craig Walls', 69.00, 15, '编程'),
                                                             ('数据结构与算法分析', 'Mark Allen Weiss', 75.00, 8, '计算机科学');

-- 测试订单数据
INSERT INTO `order` (user_id, total_price) VALUES
                                               (2, 157.50),
                                               (3, 88.50);

INSERT INTO order_item (order_id, book_id, quantity) VALUES
                                                         (1, 1, 1), -- Java编程思想
                                                         (1, 2, 1), -- Spring Boot实战
                                                         (2, 1, 1); -- Java编程思想
