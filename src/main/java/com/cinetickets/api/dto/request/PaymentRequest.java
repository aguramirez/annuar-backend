package com.cinetickets.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    
    @NotBlank(message = "Payment method ID is required")
    private String paymentMethodId;
    
    @NotBlank(message = "Payer email is required")
    @Email(message = "Invalid email format")
    private String payerEmail;
    
    private String cardToken;
    
    @Min(value = 1, message = "Installments must be at least 1")
    private Integer installments;
}