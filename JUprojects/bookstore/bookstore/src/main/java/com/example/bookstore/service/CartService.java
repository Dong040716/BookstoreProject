// CartService.java
package com.example.bookstore.service;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Cart;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
        private final CartRepository cartRepository;
        private final BookRepository bookRepository; // 添加BookRepository

        public CartService(CartRepository cartRepository, BookRepository bookRepository) {
            this.cartRepository = cartRepository;
            this.bookRepository = bookRepository; // 注入BookRepository
        }

    public List<Cart> getUserCart(User user) {
        return cartRepository.findByUser(user);
    }

    public Cart addToCart(User user, Long bookId, Integer quantity) {
            Optional<Cart> existingCartItem = cartRepository.findByUserAndBookId(user, bookId);

            // 获取图书对象
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("图书不存在: " + bookId));

            if (existingCartItem.isPresent()) {
                Cart cart = existingCartItem.get();
                cart.setQuantity(cart.getQuantity() + quantity);
                return cartRepository.save(cart);
            } else {
                Cart newCartItem = new Cart();
                newCartItem.setUser(user);
                newCartItem.setBook(book); // 设置图书对象
                newCartItem.setQuantity(quantity);
                return cartRepository.save(newCartItem);
            }
        }

    public Cart updateCartItemQuantity(User user, Long bookId, Integer quantity) {
        Cart cartItem = cartRepository.findByUserAndBookId(user, bookId)
                .orElseThrow(() -> new RuntimeException("购物车项不存在"));

        cartItem.setQuantity(quantity);
        return cartRepository.save(cartItem);
    }

    public void removeFromCart(User user, Long bookId) {
        cartRepository.deleteByUserAndBookId(user.getId(), bookId);
    }

    public void clearUserCart(User user) {
        cartRepository.deleteByUserId(user.getId());
    }

    public Integer getCartItemCount(User user) {
        return cartRepository.countByUser(user);
    }
}
