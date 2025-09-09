// CartRepository.java
package com.example.bookstore.repository;

import com.example.bookstore.entity.Cart;
import com.example.bookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByUser(User user);

    Optional<Cart> findByUserAndBookId(User user, Long bookId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.user.id = :userId AND c.book.id = :bookId")
    void deleteByUserAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Cart c WHERE c.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    Integer countByUser(User user);
}