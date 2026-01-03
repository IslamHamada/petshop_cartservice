package com.islamhamada.petshop.model;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateCartItemCountRequest {
    @Positive
    int count;
}
