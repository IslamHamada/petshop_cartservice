package com.islamhamada.petshop.controller;

import com.islamhamada.petshop.entity.CartItem;
import com.islamhamada.petshop.model.AddCartItemRequest;
import com.islamhamada.petshop.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cartService;

    @PreAuthorize("hasAnyRole('Customer')")
    @PostMapping
    private ResponseEntity<String> addCartItem(@RequestBody AddCartItemRequest request){
        String rv = cartService.addCartItem(request);
        return new ResponseEntity<>(rv, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Customer')")
    @RequestMapping("/{user_id}")
    private ResponseEntity<List<CartItem>> getCartByUser(@PathVariable String user_id){
        List<CartItem> user_cart = cartService.getUserCart(user_id);
        List<Cart> user_cart = cartService.getUserCart(user_id);
        return new ResponseEntity<>(user_cart, HttpStatus.OK);
    }
}
