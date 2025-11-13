package com.islamhamada.petshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
public class Cart {
    @Id
    @GeneratedValue
    private long id;

    private String user_id;

    private long product_id;

    private int count;
}
