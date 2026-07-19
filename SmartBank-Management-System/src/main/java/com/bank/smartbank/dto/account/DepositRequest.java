package com.bank.smartbank.dto.account;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequest {

	@NotBlank(message = "Account number is required")
	private String accountNumber;

	@NotNull(message = "Amount is required")
	@Positive(message = "Amount must be positive")
	@DecimalMin(value = "1.0", message = "Minimum deposit amount is ₹1")
	@DecimalMax(value = "1000000.0", message = "Maximum deposit amount is ₹10,00,000")
	private BigDecimal amount;

	@Size(max = 500, message = "Description cannot exceed 500 characters")
	private String description;

}
