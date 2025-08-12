package com.example.bookstore.service;

import com.example.bookstore.entity.Book;
import com.example.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// @Service 表示这是一个业务逻辑层组件
@Service
public class BookService {
    private final BookRepository bookRepository;

    // 构造方法注入 BookRepository（推荐方式）
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    // 查询所有书籍
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    // 根据 ID 查询书籍
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }
    // 保存（新增或更新）书籍
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }
    // 删除书籍
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
