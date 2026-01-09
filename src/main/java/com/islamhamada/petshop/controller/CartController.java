package com.islamhamada.petshop.controller;

import com.islamhamada.petshop.contracts.dto.ElaborateCartItemDTO;
import com.islamhamada.petshop.model.AddCartItemRequest;
import com.islamhamada.petshop.model.AddSessionCartRequest;
import com.islamhamada.petshop.model.UpdateCartItemCountRequest;
import com.islamhamada.petshop.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
    public ResponseEntity<Long> addCartItem(@Valid @RequestBody AddCartItemRequest request){
        long rv = cartService.addCartItem(request);
        return new ResponseEntity<>(rv, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Customer')")
    @GetMapping("/user/{user_id}")
    public ResponseEntity<List<ElaborateCartItemDTO>> getCartByUser(@PositiveOrZero @PathVariable long user_id){
        List<ElaborateCartItemDTO> user_cart = cartService.getUserCart(user_id);
        return new ResponseEntity<>(user_cart, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Customer')")
    @DeleteMapping("/user/{user_id}")
    public ResponseEntity<Long> emptyCartOfUser(@PositiveOrZero @PathVariable long user_id){
        long deletedRows = cartService.emptyCartOfUser(user_id);
        return new ResponseEntity<>(deletedRows, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Customer')")
    @PutMapping("/cart_item/{cart_item_id}")
    public ResponseEntity<Integer> updateCartItemCount(@PositiveOrZero @PathVariable long cart_item_id, @Valid @RequestBody UpdateCartItemCountRequest request) {
        int count = cartService.updateCartItemCount(cart_item_id, request.getCount());
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Customer')")
    @DeleteMapping("/cart_item/{cart_item_id}")
    public ResponseEntity deleteCartItem(@PositiveOrZero @PathVariable long cart_item_id) {
        cartService.deleteCartItem(cart_item_id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Customer')")
    @GetMapping("/user/item_count/{user_id}")
    public ResponseEntity<Integer> getCartItemCount(@PositiveOrZero @PathVariable long user_id) {
        int count = cartService.getCartItemCount(user_id);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('Customer')")
    @PostMapping("/login_to_checkout/{user_id}")
    public ResponseEntity addSessionCart(@PositiveOrZero @PathVariable int user_id, @Valid @RequestBody AddSessionCartRequest request){
        cartService.addSessionCart(user_id, request);
        return new ResponseEntity(HttpStatus.OK);
    }
}
