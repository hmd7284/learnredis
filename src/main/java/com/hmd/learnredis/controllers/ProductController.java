package com.hmd.learnredis.controllers;

import com.hmd.learnredis.dtos.ResponseDTO;
import com.hmd.learnredis.dtos.requests.CreateProductRequest;
import com.hmd.learnredis.dtos.requests.SearchProductRequest;
import com.hmd.learnredis.dtos.requests.UpdateProductRequest;
import com.hmd.learnredis.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO createProduct(@RequestBody @Valid CreateProductRequest request) {
        return ResponseDTO.builder()
                .message("Product created successfully")
                .data(productService.createProduct(request))
                .build();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO getProductById(@PathVariable Long id) {
        return ResponseDTO.builder()
                .message("Successfully retrieved product")
                .data(productService.getProductById(id))
                .build();
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO searchProducts(@ModelAttribute SearchProductRequest request) {
        return ResponseDTO.builder()
                .message("Successfully retrieved products")
                .data(productService.searchProducts(request))
                .build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO updateProduct(@PathVariable Long id, @RequestBody @Valid UpdateProductRequest request) {
        return ResponseDTO.builder()
                .message("Successfully updated product")
                .data(productService.updateProduct(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
