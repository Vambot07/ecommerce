package com.salihin.ecommerce.service;

import com.salihin.ecommerce.dto.ProductRequest;
import com.salihin.ecommerce.dto.ProductResponse;
import com.salihin.ecommerce.entity.Product;
import com.salihin.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductResponse createdProduct(ProductRequest productRequest) {
        Product product  = Product.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .price(productRequest.getPrice())
                .stockQuantity(productRequest.getStockQuantity())
                .build();

        Product savedProduct = productRepository.save(product);

        // Map the saved entity to our new response DTO
        return mapToProductResponse(savedProduct);
    }

    // Helper method to convert Entity -> DTO
    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .createdAt(product.getCreatedAt())
                .updateAt(product.getUpdatedAt())
                .build();
    }
}
