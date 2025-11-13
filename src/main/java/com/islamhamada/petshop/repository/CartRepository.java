package com.islamhamada.petshop.repository;

import com.islamhamada.petshop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserIdAndProductId(String user_id, long product_id);
    List<Cart> findByUserId(String user_id);
}
