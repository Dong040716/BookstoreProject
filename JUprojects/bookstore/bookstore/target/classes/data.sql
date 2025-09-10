-- 创建数据库
CREATE DATABASE IF NOT EXISTS bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookstore;

-- 用户表（管理员、顾客）
CREATE TABLE user (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(50) NOT NULL UNIQUE,
                      password VARCHAR(100) NOT NULL,
                      role ENUM('admin', 'customer') NOT NULL DEFAULT 'customer',
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
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 订单表 (使用反引号因为order是MySQL关键字)
CREATE TABLE `order` (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         user_id BIGINT NOT NULL,
                         order_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         total_price DECIMAL(10,2) NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 订单项表
CREATE TABLE order_item (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            order_id BIGINT NOT NULL,
                            book_id BIGINT NOT NULL,
                            quantity INT NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            CONSTRAINT fk_orderitem_order FOREIGN KEY (order_id) REFERENCES `order`(id) ON DELETE CASCADE,
                            CONSTRAINT fk_orderitem_book FOREIGN KEY (book_id) REFERENCES book(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 插入管理员和顾客测试数据
INSERT INTO user (username, password, role) VALUES
                                                ('admin', '123456', 'admin'),
                                                ('alice', '123456', 'customer'),
                                                ('bob', '123456', 'customer');

-- 插入图书测试数据
INSERT INTO book (title, author, price, stock, category) VALUES
                                                             ('Java编程思想', 'Bruce Eckel', 88.50, 10, '编程'),
                                                             ('Spring Boot实战', 'Craig Walls', 69.00, 15, '编程'),
                                                             ('数据结构与算法分析', 'Mark Allen Weiss', 75.00, 8, '计算机科学'),
                                                             ('深入理解计算机系统', 'Randal E. Bryant', 99.00, 5, '计算机科学'),
                                                             ('设计模式', 'Erich Gamma', 65.50, 12, '编程'),
                                                             ('Clean Code', 'Robert C. Martin', 59.00, 20, '编程'),
                                                             ('算法导论', 'Thomas H. Cormen', 128.00, 3, '计算机科学'),
                                                             ('Effective Java', 'Joshua Bloch', 79.00, 7, '编程');

-- 插入订单测试数据
INSERT INTO `order` (user_id, order_date, total_price) VALUES
                                                           (2, '2023-10-01 14:30:00', 157.50), -- Alice的订单
                                                           (3, '2023-10-02 10:15:00', 88.50),  -- Bob的订单
                                                           (2, '2023-10-03 16:45:00', 203.00); -- Alice的另一个订单

-- 插入订单项测试数据
INSERT INTO order_item (order_id, book_id, quantity) VALUES
                                                         (1, 1, 1), -- Alice购买1本Java编程思想
                                                         (1, 2, 1), -- Alice购买1本Spring Boot实战
                                                         (2, 1, 1), -- Bob购买1本Java编程思想
                                                         (3, 4, 1), -- Alice购买1本深入理解计算机系统
                                                         (3, 5, 2); -- Alice购买2本设计模式

-- 创建索引以提高查询性能
CREATE INDEX idx_user_username ON user(username);
CREATE INDEX idx_book_title ON book(title);
CREATE INDEX idx_book_category ON book(category);
CREATE INDEX idx_order_user ON `order`(user_id);
CREATE INDEX idx_order_date ON `order`(order_date);
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
SELECT * FROM `order`;
SELECT * FROM order_item;