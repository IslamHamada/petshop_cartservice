package com.islamhamada.petshop.controller;

import com.islamhamada.petshop.contracts.CartItemDTO;
import com.islamhamada.petshop.model.AddCartItemRequest;
import com.islamhamada.petshop.service.CartService;
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
    public ResponseEntity<Long> addCartItem(@RequestBody AddCartItemRequest request){
        long rv = cartService.addCartItem(request);
        return new ResponseEntity<>(rv, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Customer')")
    @GetMapping("/{user_id}")
    public ResponseEntity<List<CartItemDTO>> getCartByUser(@PathVariable long user_id){
        List<CartItemDTO> user_cart = cartService.getUserCart(user_id);
        return new ResponseEntity<>(user_cart, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Customer')")
    @DeleteMapping("/{user_id}")
    public ResponseEntity<Long> emptyCartOfUser(@PathVariable long user_id){
        long deletedRows = cartService.emptyCartOfUser(user_id);
        return new ResponseEntity<>(deletedRows, HttpStatus.OK);
    }
}
