package com.islamhamada.petshop.model;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdateCartItemCountRequest {
    @Positive
    int count;
}
