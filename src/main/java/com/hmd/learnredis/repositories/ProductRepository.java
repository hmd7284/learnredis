package com.hmd.learnredis.repositories;

import com.hmd.learnredis.models.Product;
import com.hmd.learnredis.repositories.custom.CustomProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, CustomProductRepository {
    boolean existsByName(String name);
}
