package com.islamhamada.petshop.service;

import com.islamhamada.petshop.contracts.CartItemDTO;
import com.islamhamada.petshop.entity.CartItem;
import com.islamhamada.petshop.model.AddCartItemRequest;
import com.islamhamada.petshop.repository.CartItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    CartItemRepository cartItemRepository;

    @Override
    public long addCartItem(AddCartItemRequest request) {
        Optional<CartItem> cart_item_optional = cartItemRepository.findByUserIdAndProductId(request.getBackend_id(), request.getProduct_id());
        if(cart_item_optional.isPresent()){
            CartItem cart_item = cart_item_optional.get();
            cart_item.setCount(cart_item.getCount() + request.getCount());
            cartItemRepository.save(cart_item);
            return cart_item.getId();
        } else {
            CartItem new_cart_item = CartItem.builder()
                    .userId(request.getBackend_id())
                    .productId(request.getProduct_id())
                    .count(request.getCount())
                    .build();

            cartItemRepository.save(new_cart_item);
            return new_cart_item.getId();
        }
    }

    @Override
    public List<CartItemDTO> getUserCart(long user_id) {
        List<CartItem> cartItems =  cartItemRepository.findByUserId(user_id);
        List<CartItemDTO> cartItemsDTO = cartItems.stream()
                .map(cartItem ->
                    CartItemDTO.builder()
                            .id(cartItem.getId())
                            .userId(cartItem.getUserId())
                            .productId(cartItem.getProductId())
                            .count(cartItem.getCount())
                            .build()
                ).toList();
        return cartItemsDTO;
    }

    @Override
    @Transactional
    public long emptyCartOfUser(long userId) {
        return cartItemRepository.deleteByUserId(userId);
    }

    @Override
    public int updateCartItemCount(long cart_item_id, int count) {
        CartItem cartItem = cartItemRepository.findById(cart_item_id).get();
        cartItem.setCount(count);
        cartItem = cartItemRepository.save(cartItem);
        return cartItemRepository.save(cartItem).getCount();
    }
}
