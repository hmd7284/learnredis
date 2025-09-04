package com.hmd.learnredis.services;

import com.hmd.learnredis.dtos.ProductDTO;
import com.hmd.learnredis.dtos.requests.CreateProductRequest;
import com.hmd.learnredis.dtos.requests.SearchProductRequest;
import com.hmd.learnredis.dtos.requests.UpdateProductRequest;
import com.hmd.learnredis.dtos.responses.Meta;
import com.hmd.learnredis.dtos.responses.PaginatedResponse;
import com.hmd.learnredis.exceptions.AlreadyExistsException;
import com.hmd.learnredis.exceptions.NotFoundException;
import com.hmd.learnredis.mappers.ProductMapper;
import com.hmd.learnredis.models.Product;
import com.hmd.learnredis.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final RedisService redisService;

    @Transactional
    public PaginatedResponse searchProducts(SearchProductRequest request) {
        Page<ProductDTO> results = productRepository.searchProducts(request).map(productMapper::toProductDTO);
        return PaginatedResponse.builder()
                .meta(Meta.builder()
                        .page(results.getNumber() + 1)
                        .size(results.getSize())
                        .totalPages(results.getTotalPages())
                        .totalElements(results.getTotalElements())
                        .build())
                .data(results.getContent())
                .build();
    }

    @Transactional
    public ProductDTO createProduct(CreateProductRequest request) {
        if (productRepository.findByName(request.getName()).isPresent())
            throw new AlreadyExistsException(String.format("Product %s already exists", request.getName()));
        Product newProduct = Product.builder()
                .name(request.getName())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .build();
        Product savedProduct = productRepository.save(newProduct);
        ProductDTO productDTO = productMapper.toProductDTO(savedProduct);
        redisService.put(String.format("product:%s", savedProduct.getId()), productDTO);
        return productDTO;
    }

    @Transactional
    public ProductDTO updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Product %s not found", id)));
        if (!product.getName().equals(request.getName())) {
            Optional<Product> existingProduct = productRepository.findByName(request.getName());
            if (existingProduct.isPresent())
                throw new AlreadyExistsException(String.format("Product %s already exists", request.getName()));
            product.setName(request.getName());
        }
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        Product updatedProduct = productRepository.save(product);
        ProductDTO productDTO = productMapper.toProductDTO(updatedProduct);
        redisService.put(String.format("product:%s", updatedProduct.getId()), productDTO);
        return productDTO;
    }

    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
        redisService.delete(String.format("product:%s", id));
    }

    @Transactional
    public ProductDTO getProductById(Long id) {
        String key = "product:" + id;
        Optional<Object> value = redisService.get(key);
        if (value.isPresent())
            return (ProductDTO) value.get();
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("Product %s not found", id)));
        return productMapper.toProductDTO(product);
    }
}
