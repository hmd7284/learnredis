package com.hmd.learnredis.repositories.custom;

import com.hmd.learnredis.dtos.ProductDTO;
import com.hmd.learnredis.dtos.requests.SearchProductRequest;
import com.hmd.learnredis.models.Product;
import org.springframework.data.domain.Page;

public interface CustomProductRepository {
    Page<Product> searchProducts(SearchProductRequest request);
}
