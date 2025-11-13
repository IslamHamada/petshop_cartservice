package com.islamhamada.petshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
public class CartItem {
    @Id
    @GeneratedValue
    private long id;

    private long userId;

    private long productId;

    private int count;
}
