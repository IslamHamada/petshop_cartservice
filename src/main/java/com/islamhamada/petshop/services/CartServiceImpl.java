package com.islamhamada.petshop.services;

import com.islamhamada.petshop.entity.Cart;
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
    public String addCartItem(AddCartItemRequest request) {
        Optional<Cart> cart_item_optional = cartRepository.findByUserIdAndProductId(request.getUser_id(), request.getProduct_id());
        if(cart_item_optional.isPresent()){
            Cart cart_item = cart_item_optional.get();
            cart_item.setCount(cart_item.getCount() + request.getCount());
            cartRepository.save(cart_item);
            return "updated cart item with id: " + cart_item.getId();
        } else {
            Cart new_cart_item = Cart.builder()
                    .user_id(request.getUser_id())
                    .product_id(request.getProduct_id())
                    .count(request.getCount())
                    .build();

            cartRepository.save(new_cart_item);
            return "created cart item with id: " + new_cart_item.getId();
        }
    }

    @Override
    public List<Cart> getUserCart(String user_id) {
        return cartRepository.findByUserId(user_id);
    }
}
