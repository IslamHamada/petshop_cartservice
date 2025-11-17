package com.islamhamada.petshop.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddCartItemRequest {
    private long backend_id;
    private long product_id;
    private int count;
}
