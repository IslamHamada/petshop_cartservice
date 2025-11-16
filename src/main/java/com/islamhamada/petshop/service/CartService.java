package com.islamhamada.petshop.service;

import com.islamhamada.petshop.contracts.CartItemDTO;
import com.islamhamada.petshop.model.AddCartItemRequest;

import java.util.List;

public interface CartService {
    long addCartItem(AddCartItemRequest request);
    List<CartItemDTO> getUserCart(long user_id);
    long emptyCartOfUser(long userId);
}
