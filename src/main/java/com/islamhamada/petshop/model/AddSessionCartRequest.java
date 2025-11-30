package com.islamhamada.petshop.model;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class AddSessionCartRequest {
    @Data
    public static class SessionCartItem {
        @Positive
        long product_id;
        @Positive
        int count;
    }
    @Size(min = 1)
    List<SessionCartItem> cart_items;
}


