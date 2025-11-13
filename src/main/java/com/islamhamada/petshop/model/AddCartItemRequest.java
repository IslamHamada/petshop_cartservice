package com.islamhamada.petshop.model;

import lombok.Data;

@Data
public class AddCartItemRequest {
    private long user_id;
    private long product_id;
    private int count;
}
