package com.example.bookstore.repository;

import com.example.bookstore.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;


// Repository 层接口继承 JpaRepository<T, ID>
// 第一个泛型参数是实体类类型，第二个是主键类型
// JpaRepository 已经帮我们实现了常用的数据库操作（增删改查）
public interface BookRepository extends JpaRepository<Book, Long> {
    // 如果需要自定义查询，可以在这里写方法，比如：
    // List<Book> findByAuthor(String author);
}
