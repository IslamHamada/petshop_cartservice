package com.islamhamada.petshop.model;

import lombok.Data;

import java.util.List;

@Data
public class AddSessionCartRequest {
    @Data
    public static class SessionCartItem {
        long product_id;
        int count;
    }

    List<SessionCartItem> cart_items;
}


