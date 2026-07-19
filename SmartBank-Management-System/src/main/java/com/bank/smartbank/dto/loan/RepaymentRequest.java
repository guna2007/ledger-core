package com.bank.smartbank.dto.loan;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepaymentRequest {

	@NotBlank(message = "Account number is required")
	private String accountNumber;

	@NotNull(message = "Amount is required")
	@Positive(message = "Amount must be positive")
	@DecimalMin(value = "1.0", message = "Minimum repayment amount is ₹1")
	@DecimalMax(value = "10000000.0", message = "Maximum repayment amount is ₹1,00,00,000")
	private BigDecimal amount;

}
