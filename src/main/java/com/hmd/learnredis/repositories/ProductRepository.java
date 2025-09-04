package com.hmd.learnredis.repositories;

import com.hmd.learnredis.models.Product;
import com.hmd.learnredis.repositories.custom.CustomProductRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, CustomProductRepository {
    Optional<Product> findByName(String name);
}
