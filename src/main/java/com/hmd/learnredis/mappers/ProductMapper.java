package com.hmd.learnredis.mappers;

import com.hmd.learnredis.dtos.ProductDTO;
import com.hmd.learnredis.models.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductDTO toProductDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .productName(product.getName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }
}
