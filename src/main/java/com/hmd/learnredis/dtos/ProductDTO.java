package com.hmd.learnredis.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String productName;
    private Double price;
    private Long quantity;
}
