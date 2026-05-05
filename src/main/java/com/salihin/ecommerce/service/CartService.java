package com.salihin.ecommerce.service;

import com.salihin.ecommerce.dto.Cart;
import com.salihin.ecommerce.dto.CartItem;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class CartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CART_KEY_PREFIX = "cart:";

    public CartService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Cart getCart(String userId) {
        String key = CART_KEY_PREFIX + userId;
        Cart cart = (Cart) redisTemplate.opsForValue().get(key);
        return cart != null ? cart : new Cart(userId, new java.util.ArrayList<>());
    }

    public void saveCart(String userId, Cart cart) {
        String key = CART_KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(key, cart, 24, TimeUnit.HOURS); // Cart expires in 24 hours
    }

    public void deleteCart(String userId) {
        redisTemplate.delete(CART_KEY_PREFIX + userId);
    }

    public Cart addItemToCart(String userId, CartItem newItem) {
        Cart cart = getCart(userId);
        
        // Check if the product is already in the cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(newItem.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // If it exists, just update the quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + newItem.getQuantity());
        } else {
            // If it doesn't exist, add it to the cart
            cart.getItems().add(newItem);
        }

        saveCart(userId, cart);
        return cart;
    }

    public Cart removeItemFromCart(String userId, Long productId) {
        Cart cart = getCart(userId);
        
        // Remove the item where the productId matches
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        
        saveCart(userId, cart);
        return cart;
    }
}
