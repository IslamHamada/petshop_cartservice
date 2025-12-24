package com.islamhamada.petshop.service;

import com.islamhamada.petshop.contracts.dto.ElaborateCartItemDTO;
import com.islamhamada.petshop.contracts.dto.ProductDTO;
import com.islamhamada.petshop.entity.CartItem;
import com.islamhamada.petshop.exception.CartServiceException;
import com.islamhamada.petshop.external.service.ProductService;
import com.islamhamada.petshop.model.AddCartItemRequest;
import com.islamhamada.petshop.model.AddSessionCartRequest;
import com.islamhamada.petshop.repository.CartItemRepository;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class CartServiceImpl implements CartService{

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ProductService productService;

    @Override
    public long addCartItem(AddCartItemRequest request) {
        log.info("Adding cartItem of product with id: " + request.getProduct_id() + ", of amount: " + request.getCount());
        Optional<CartItem> cart_item_optional = cartItemRepository.findByUserIdAndProductId(request.getBackend_id(), request.getProduct_id());
        CartItem cart_item;
        if(cart_item_optional.isPresent()){
            cart_item = cart_item_optional.get();
            cart_item.setCount(cart_item.getCount() + request.getCount());
        } else {
            cart_item = CartItem.builder()
                    .userId(request.getBackend_id())
                    .productId(request.getProduct_id())
                    .count(request.getCount())
                    .build();
        }
        cartItemRepository.save(cart_item);
        log.info("CartItem successfully added");
        return cart_item.getId();
    }

    @Override
    public List<ElaborateCartItemDTO> getUserCart(long user_id) {
        log.info("Getting cart of user with id: " + user_id);
        List<CartItem> cartItems =  cartItemRepository.findByUserId(user_id);
        List<ElaborateCartItemDTO> elaborateCartItems = cartItems.stream()
                .map(cartItem -> {
                    log.info("Calling product service to fetch product info with id: " + cartItem.getProductId());
                    ProductDTO product = productService.getProductById(cartItem.getProductId()).getBody();
                    return ElaborateCartItemDTO.builder()
                            .product_id(product.getId())
                            .product_name(product.getName())
                            .product_price(product.getPrice())
                            .product_image(product.getImage())
                            .cart_item_id(cartItem.getId())
                            .cart_item_count(cartItem.getCount())
                            .build();
                        }).toList();
        log.info("Cart successfully fetched");
        return elaborateCartItems;
    }

    @Override
    @Transactional
    public long emptyCartOfUser(long userId) {
        log.info("Emptying cart of user with id: " + userId);
        long rv = cartItemRepository.deleteByUserId(userId);
        log.info("Cart successfully emptied");
        return rv;
    }

    @Override
    public int updateCartItemCount(long cart_item_id, int count) {
        log.info("Updating cart item count of id: " + cart_item_id + ", to count: " + count);
        CartItem cartItem = cartItemRepository.findById(cart_item_id).orElseThrow(() ->
                new CartServiceException("No cart item with id: " + cart_item_id, "NOT_FOUND", HttpStatus.NOT_FOUND)
        );
        cartItem.setCount(count);
        cartItem = cartItemRepository.save(cartItem);
        log.info("Cart item count successfully updated");
        return cartItem.getCount();
    }

    @Override
    @Transactional
    public void deleteCartItem(long cart_item_id) {
        log.info("Deleting cart item of id: " + cart_item_id);
        cartItemRepository.deleteById(cart_item_id);
        log.info("Cart item successfully deleted");
    }

    @Override
    public int getCartItemCount(long user_id) {
        log.info("Getting total cart items count of user with id: " + user_id);
        int rv = cartItemRepository.sumCountByUserId(user_id);
        log.info("Total cart item count successfully fetched");
        return rv;
    }

    @Override
    public void addSessionCart(long user_id, AddSessionCartRequest request) {
        log.info("Adding session cart to currently logged in user with id: " + user_id);
        request.getCart_items().forEach(cart_item -> {
            addCartItem(AddCartItemRequest.builder()
                    .backend_id(user_id)
                    .product_id(cart_item.getProduct_id())
                    .count(cart_item.getCount())
                    .build());
        });
        log.info("Session cart added to the user successfully");
    }
}
