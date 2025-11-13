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
    private ResponseEntity<Long> addCartItem(@RequestBody AddCartItemRequest request){
        long rv = cartService.addCartItem(request);
        return new ResponseEntity<>(rv, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Customer')")
    @GetMapping("/{user_id}")
    private ResponseEntity<List<CartItem>> getCartByUser(@PathVariable long user_id){
        List<CartItem> user_cart = cartService.getUserCart(user_id);
        return new ResponseEntity<>(user_cart, HttpStatus.OK);
    }
}
