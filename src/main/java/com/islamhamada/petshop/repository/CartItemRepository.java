package com.islamhamada.petshop.repository;

import com.islamhamada.petshop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByUserIdAndProductId(long user_id, long product_id);
    List<CartItem> findByUserId(long user_id);
    long deleteByUserId(long user_id);
    void deleteById(long cart_item_id);
}
