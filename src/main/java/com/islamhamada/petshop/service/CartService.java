package com.islamhamada.petshop.service;

import com.islamhamada.petshop.contracts.ElaborateCartItemDTO;
import com.islamhamada.petshop.model.AddCartItemRequest;
import com.islamhamada.petshop.model.AddSessionCartRequest;

import java.util.List;

public interface CartService {
    long addCartItem(AddCartItemRequest request);
    List<ElaborateCartItemDTO> getUserCart(long user_id);
    long emptyCartOfUser(long userId);
    int updateCartItemCount(long cart_item_id, int count);
    void deleteCartItem(long cart_item_id);
    int getCartItemCount(long user_id);
    void addSessionCart(long user_id, AddSessionCartRequest request);
}
