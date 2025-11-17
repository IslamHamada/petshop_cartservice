package com.islamhamada.petshop.service;

import com.islamhamada.petshop.contracts.CartItemDTO;
import com.islamhamada.petshop.model.AddCartItemRequest;

import java.util.List;

public interface CartService {
    long addCartItem(AddCartItemRequest request);
    List<CartItemDTO> getUserCart(long user_id);
    long emptyCartOfUser(long userId);
    int updateCartItemCount(long cart_item_id, int count);
    void deleteCartItem(long cart_item_id);
    int getCartItemCount(long user_id);
}
