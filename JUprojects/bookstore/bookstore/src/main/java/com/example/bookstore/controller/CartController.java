// CartController.java
package com.example.bookstore.controller;

import com.example.bookstore.entity.Cart;
import com.example.bookstore.entity.User;
import com.example.bookstore.service.CartService;
import com.example.bookstore.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Cart>> getCart(@RequestParam Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        List<Cart> cartItems = cartService.getUserCart(user);
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(
            @RequestParam Long userId,
            @RequestParam Long bookId,
            @RequestParam(defaultValue = "1") Integer quantity) {

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        try {
            Cart cartItem = cartService.addToCart(user, bookId, quantity);
            return ResponseEntity.ok(cartItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<Cart> updateCartItem(
            @RequestParam Long userId,
            @RequestParam Long bookId,
            @RequestParam Integer quantity) {

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Cart cartItem = cartService.updateCartItemQuantity(user, bookId, quantity);
        return ResponseEntity.ok(cartItem);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFromCart(
            @RequestParam Long userId,
            @RequestParam Long bookId) {

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        cartService.removeFromCart(user, bookId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(@RequestParam Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        cartService.clearUserCart(user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCartItemCount(@RequestParam Long userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Integer count = cartService.getCartItemCount(user);
        return ResponseEntity.ok(count);
    }
}