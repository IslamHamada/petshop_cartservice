package com.islamhamada.petshop.services;

import com.islamhamada.petshop.entity.CartItem;
import com.islamhamada.petshop.model.AddCartItemRequest;
import com.islamhamada.petshop.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService{

    @Autowired
    CartRepository cartRepository;

    @Override
    public long addCartItem(AddCartItemRequest request) {
        Optional<CartItem> cart_item_optional = cartRepository.findByUserIdAndProductId(request.getUser_id(), request.getProduct_id());
        if(cart_item_optional.isPresent()){
            CartItem cart_item = cart_item_optional.get();
            cart_item.setCount(cart_item.getCount() + request.getCount());
            cartRepository.save(cart_item);
//            return "updated cart item with id: " + cart_item.getId();
            return cart_item.getId();
        } else {
            CartItem new_cart_item = CartItem.builder()
                    .userId(request.getUser_id())
                    .productId(request.getProduct_id())
                    .count(request.getCount())
                    .build();

            cartRepository.save(new_cart_item);
//            return "created cart item with id: " + new_cart_item.getId();
            return new_cart_item.getId();
        }
    }

    @Override
    public List<CartItem> getUserCart(long user_id) {
        return cartRepository.findByUserId(user_id);
    }
}
