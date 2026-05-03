package com.salihin.ecommerce.controller;

import com.salihin.ecommerce.dto.ProductRequest;
import com.salihin.ecommerce.dto.ProductResponse;
import com.salihin.ecommerce.entity.Product;
import com.salihin.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
