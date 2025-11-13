package com.islamhamada.petshop.services;

import com.islamhamada.petshop.entity.CartItem;
import com.islamhamada.petshop.model.AddCartItemRequest;

import java.util.List;

public interface CartService {
    String addCartItem(AddCartItemRequest request);
    List<CartItem> getUserCart(String user_id);
}
