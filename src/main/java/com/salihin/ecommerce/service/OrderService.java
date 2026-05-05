package com.salihin.ecommerce.service;

import com.salihin.ecommerce.dto.Cart;
import com.salihin.ecommerce.entity.Order;
import com.salihin.ecommerce.entity.OrderItem;
import com.salihin.ecommerce.entity.OrderStatus;
import com.salihin.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;

    @Transactional
    public Order placeOrder(String userId) {
        // 1. Fetch the user's cart
        Cart cart = cartService.getCart(userId);

        // 2. Validate that the cart is not empty
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cannot place order: Cart is empty");
        }

        // 3. Create the new Order
        Order order = Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .totalAmount(cart.getTotal())
                .build();

        // 4. Convert CartItems to OrderItems and add them to the Order
        cart.getItems().forEach(cartItem -> {
            OrderItem orderItem = OrderItem.builder()
                    .productId(cartItem.getProductId())
                    .productName(cartItem.getProductName())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getPrice())
                    .build();
            
            // This helper method also sets the order reference in the OrderItem
            order.addItem(orderItem);
        });

        // 5. Save the order to PostgreSQL
        Order savedOrder = orderRepository.save(order);

        // 6. Clear the cart in Redis
        cartService.deleteCart(userId);

        return savedOrder;
    }

    public List<Order> getUserOrders(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
