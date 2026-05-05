package com.salihin.ecommerce.controller;

import com.salihin.ecommerce.dto.Cart;
import com.salihin.ecommerce.dto.CartItem;
import com.salihin.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Cart> getCart(Principal principal) {
        String userId = principal.getName();
        Cart cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(Principal principal, @RequestBody CartItem cartItem) {
        String userId = principal.getName();
        Cart cart = cartService.addItemToCart(userId, cartItem);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Cart> removeFromCart(Principal principal, @PathVariable Long productId) {
        String userId = principal.getName();
        Cart cart = cartService.removeItemFromCart(userId, productId);
        return ResponseEntity.ok(cart);
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart(Principal principal) {
        String userId = principal.getName();
        cartService.deleteCart(userId);
        return ResponseEntity.ok().build();
    }
}
