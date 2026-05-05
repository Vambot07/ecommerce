package com.salihin.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    // In a real scenario, you would send a tokenized card from the frontend (like a Stripe Token), 
    // but for our mock, we can accept dummy card details to make the frontend feel realistic.
    private String cardNumber;
    private String expiryDate;
    private String cvv;
}
