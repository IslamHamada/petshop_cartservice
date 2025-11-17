package com.islamhamada.petshop.service;

import com.islamhamada.petshop.contracts.CartItemDTO;
import com.islamhamada.petshop.contracts.ElaborateCartItemDTO;
import com.islamhamada.petshop.contracts.ProductDTO;
import com.islamhamada.petshop.entity.CartItem;
import com.islamhamada.petshop.external.service.ProductService;
import com.islamhamada.petshop.model.AddCartItemRequest;
import com.islamhamada.petshop.model.AddSessionCartRequest;
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

    @Autowired
    ProductService productService;

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
    public List<ElaborateCartItemDTO> getUserCart(long user_id) {
        List<CartItem> cartItems =  cartItemRepository.findByUserId(user_id);
        List<ElaborateCartItemDTO> elaborateCartItems = cartItems.stream()
                .map(cartItem -> {
                    ProductDTO product = productService.getProductById(cartItem.getProductId()).getBody();
                    return ElaborateCartItemDTO.builder()
                            .product_name(product.getName())
                            .product_price(product.getPrice())
                            .product_id(product.getId())
                            .cart_item_id(cartItem.getId())
                            .cart_item_count(cartItem.getCount())
                            .build();
                        }).toList();
        return elaborateCartItems;
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

    @Override
    @Transactional
    public void deleteCartItem(long cart_item_id) {
        cartItemRepository.deleteById(cart_item_id);
    }

    @Override
    public int getCartItemCount(long user_id) {
        return cartItemRepository.sumCountByUserId(user_id);
    }

    @Override
    public void addSessionCart(long user_id, AddSessionCartRequest request) {
        request.getCart_items().forEach(cart_item -> {
            addCartItem(AddCartItemRequest.builder()
                    .backend_id(user_id)
                    .product_id(cart_item.getProduct_id())
                    .count(cart_item.getCount())
                    .build());
        });
    }
}
