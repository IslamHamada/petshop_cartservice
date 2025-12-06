package com.islamhamada.petshop.model;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddSessionCartRequest {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SessionCartItem {
        @Positive
        long product_id;
        @Positive
        int count;
    }
    @Size(min = 1)
    List<SessionCartItem> cart_items;
}


