package com.hmd.learnredis.models;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_products_name", columnList = "name")
})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "quantity", nullable = false)
    private Long quantity;
}
