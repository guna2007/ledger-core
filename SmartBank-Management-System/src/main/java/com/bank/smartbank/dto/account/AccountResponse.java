package com.bank.smartbank.dto.account;

import com.bank.smartbank.entity.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AccountResponse {

	private Long id;
	private String accountNumber;
	private BigDecimal balance;
	private String type;
	private String status;
	private LocalDateTime createdAt;

	public AccountResponse(Account account) {
		this.id = account.getId();
		this.accountNumber = account.getAccountNumber();
		this.balance = account.getBalance();
		this.type = account.getType().name();
		this.status = account.getStatus().name();
		this.createdAt = account.getCreatedAt();
	}
}
