-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookstore;

-- 用户表（管理员、顾客）
CREATE TABLE user (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(50) NOT NULL UNIQUE,
                      password VARCHAR(100) NOT NULL,
                      role ENUM('admin', 'customer') NOT NULL DEFAULT 'customer',
                      email VARCHAR(100),
                      phone VARCHAR(20),
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 图书表
CREATE TABLE book (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      title VARCHAR(100) NOT NULL,
                      author VARCHAR(100) NOT NULL,
                      price DECIMAL(10,2) NOT NULL,
                      stock INT NOT NULL DEFAULT 0,
                      category VARCHAR(50),
                      description TEXT,
                      image_url VARCHAR(255),
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 订单表 (使用反引号因为order是MySQL关键字)
CREATE TABLE `order` (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         user_id BIGINT NOT NULL,
                         order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         total_price DECIMAL(10,2) NOT NULL,
                         status ENUM('pending', 'processing', 'completed', 'cancelled') DEFAULT 'pending',
    -- 添加收货信息字段（即使前端不需要，数据库也应该有这些字段）
                         shipping_address VARCHAR(255),
                         recipient_name VARCHAR(100),
                         recipient_phone VARCHAR(20),
    -- 添加支付信息字段
                         payment_method ENUM('credit_card', 'debit_card', 'paypal', 'wechat', 'alipay', 'cash') DEFAULT 'cash',
                         payment_status ENUM('pending', 'paid', 'failed') DEFAULT 'pending',
                         payment_date DATETIME,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 订单项表 - 修复版本
CREATE TABLE order_item (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            order_id BIGINT NOT NULL,
                            book_id BIGINT NOT NULL,
                            quantity INT NOT NULL,
                            unit_price DECIMAL(10,2) NOT NULL, -- 添加单价字段，防止价格变动影响历史订单
                            subtotal DECIMAL(10,2) NOT NULL, -- 添加小计字段
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES `order`(id) ON DELETE CASCADE,
                            CONSTRAINT fk_orderitem_book FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 插入管理员和顾客测试数据
INSERT INTO user (username, password, role, email, phone) VALUES
                                                              ('admin', '123456', 'admin', 'admin@bookstore.com', '13800138000'),
                                                              ('alice', '123456', 'customer', 'alice@example.com', '13900139000'),
                                                              ('bob', '123456', 'customer', 'bob@example.com', '13700137000');

-- 插入图书测试数据
INSERT INTO book (title, author, price, stock, category, description) VALUES
                                                                          ('Java编程思想', 'Bruce Eckel', 88.50, 10, '编程', 'Java编程的经典著作，深入浅出地讲解Java编程思想'),
                                                                          ('Spring Boot实战', 'Craig Walls', 69.00, 15, '编程', 'Spring Boot框架的实战指南，帮助快速开发Java应用'),
                                                                          ('数据结构与算法分析', 'Mark Allen Weiss', 75.00, 8, '计算机科学', '数据结构与算法的经典教材，适合计算机专业学生'),
                                                                          ('深入理解计算机系统', 'Randal E. Bryant', 99.00, 5, '计算机科学', '计算机系统领域的经典著作，深入讲解计算机系统原理'),
                                                                          ('设计模式', 'Erich Gamma', 65.50, 12, '编程', '软件设计模式的经典著作，GOF23种设计模式详解'),
                                                                          ('Clean Code', 'Robert C. Martin', 59.00, 20, '编程', '代码整洁之道，提高代码质量的实用指南'),
                                                                          ('算法导论', 'Thomas H. Cormen', 128.00, 3, '计算机科学', '算法领域的权威教材，涵盖各种算法设计与分析'),
                                                                          ('Effective Java', 'Joshua Bloch', 79.00, 7, '编程', 'Java编程最佳实践，提高Java编程水平的必备书籍');

-- 创建索引以提高查询性能
CREATE INDEX idx_user_username ON user(username);
CREATE INDEX idx_user_email ON user(email);
CREATE INDEX idx_book_title ON book(title);
CREATE INDEX idx_book_category ON book(category);
CREATE INDEX idx_book_author ON book(author);
CREATE INDEX idx_order_user ON `order`(user_id);
CREATE INDEX idx_order_date ON `order`(order_date);
CREATE INDEX idx_order_status ON `order`(status);
CREATE INDEX idx_order_item_order ON order_item(order_id);
CREATE INDEX idx_order_item_book ON order_item(book_id);

-- 显示表结构
DESC user;
DESC book;
DESC `order`;
DESC order_item;

-- 显示测试数据
SELECT * FROM user;
SELECT * FROM book;