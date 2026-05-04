package com.salihin.ecommerce.controller;

import com.salihin.ecommerce.dto.ProductRequest;
import com.salihin.ecommerce.dto.ProductResponse;
import com.salihin.ecommerce.entity.Product;
import com.salihin.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // This endpoint is secured by our SecurityConfiguration to only allow ADMINs
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request
    ) {
        ProductResponse savedProductResponse = productService.createdProduct(request);

        // Return 201 Created status
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProductResponse);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAllProducts() {
        return ResponseEntity.ok(productService.findAllProduct());
    }

    // This endpoint is secured by SecurityConfiguration to only allow ADMINs
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable("productId") Long ProductId,
            @Valid @RequestBody ProductRequest request
    ){
        ProductResponse updatedProduct = productService.updateProduct(ProductId, request);
        return ResponseEntity.ok(updatedProduct);
    }

    // This endpoint is secured by SecurityConfiguration
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable("productId") Long productId,
            @Valid @RequestBody ProductRequest request
    ) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
