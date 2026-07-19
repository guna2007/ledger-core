package com.bank.smartbank.dto.transaction;

import com.bank.smartbank.entity.Transaction;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionResponse {

	private Long id;
	private String type;
	private BigDecimal amount;
	private BigDecimal balanceAfter;
	private String description;
	private String referenceAccount;
	private String transactionRef;
	private LocalDateTime createdAt;

	public TransactionResponse(Transaction transaction) {
		this.id = transaction.getId();
		this.type = transaction.getType().name();
		this.amount = transaction.getAmount();
		this.balanceAfter = transaction.getBalanceAfter();
		this.description = transaction.getDescription();
		this.referenceAccount = transaction.getReferenceAccount();
		this.transactionRef = transaction.getTransactionRef();
		this.createdAt = transaction.getCreatedAt();
	}
}
