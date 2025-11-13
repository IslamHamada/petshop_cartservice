package com.islamhamada.petshop.services;

import com.islamhamada.petshop.entity.Cart;
import com.islamhamada.petshop.model.AddCartItemRequest;

import java.util.List;

public interface CartService {
    String addCartItem(AddCartItemRequest request);
    List<Cart> getUserCart(String user_id);
}
