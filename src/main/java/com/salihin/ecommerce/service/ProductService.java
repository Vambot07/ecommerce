package com.salihin.ecommerce.service;

import com.salihin.ecommerce.dto.ProductRequest;
import com.salihin.ecommerce.dto.ProductResponse;
import com.salihin.ecommerce.entity.Product;
import com.salihin.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<ProductResponse> findAllProduct() {
        List<Product> products = productRepository.findAll();

        // Use Java Stream to map each Product entity to a ProductResponse DTO
        return products.stream()
                .map(this::mapToProductResponse)
                .toList(); // ATAU pun .collect(Collectors.toList);
    }

    public ProductResponse updateProduct(Long productId, ProductRequest productRequest) {
        // 1. Find the product or throw an exception if not found
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));


        // 2. Update the fields
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setStockQuantity(productRequest.getStockQuantity());

        // 3. Save the updated product back to the database
        Product updatedProduct = productRepository.save(product);

        // 4. Return the mapped response
        return mapToProductResponse(updatedProduct);
    }

    public void deleteProduct(Long productId) {
        // 1. Check if the product exists
        if(!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found with id: " + productId);
        }
        // 2. Delete it
        productRepository.deleteById(productId);
    }
}
