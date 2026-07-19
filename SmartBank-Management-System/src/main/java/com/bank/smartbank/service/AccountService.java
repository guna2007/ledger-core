package com.bank.smartbank.service;

import com.bank.smartbank.dto.account.AccountResponse;
import com.bank.smartbank.dto.account.CreateAccountRequest;
import com.bank.smartbank.dto.account.DepositRequest;
import com.bank.smartbank.dto.account.WithdrawRequest;
import com.bank.smartbank.dto.transaction.TransactionResponse;
import com.bank.smartbank.entity.*;
import com.bank.smartbank.exception.*;
import com.bank.smartbank.repository.AccountRepository;
import com.bank.smartbank.repository.UserRepository;
import com.bank.smartbank.util.AccountNumberGenerator;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountService {

	private final AccountRepository accountRepository;
	private final UserRepository userRepository;
	private final AccountNumberGenerator accountNumberGenerator;
	private final TransactionService transactionService;

	public AccountService(AccountRepository accountRepository, UserRepository userRepository,
                          AccountNumberGenerator accountNumberGenerator, TransactionService transactionService) {

		this.accountRepository = accountRepository;
		this.userRepository = userRepository;
		this.accountNumberGenerator = accountNumberGenerator;
		this.transactionService = transactionService;
	}

	public AccountResponse createAccount(Long userId, CreateAccountRequest request) {
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

		String accountNumber = generateUniqueAccountNumber();

		Account account = new Account();
		account.setAccountNumber(accountNumber);
		account.setBalance(BigDecimal.ZERO);
		account.setType(AccountType.valueOf(request.getType()));
		account.setStatus(AccountStatus.ACTIVE);
		account.setUser(user);

		Account savedAccount = accountRepository.save(account);

		System.out.println("Account created :" + accountNumber);

		return new AccountResponse(savedAccount);
	}

	public List<AccountResponse> getUserAccounts(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException(userId);
		}

		List<Account> accounts = accountRepository.findByUserId(userId);

		return accounts.stream().map(AccountResponse::new).collect(Collectors.toList());
	}

	public AccountResponse getAccountNumber(String accountNumber) {
		Account account = accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new AccountNotFoundException("accountNumber", accountNumber));

		return new AccountResponse(account);
	}

	public Account getAccountEntity(String accountNumber) {
		return accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new AccountNotFoundException("accountNumber", accountNumber));
	}

	public boolean isAccountOwnedByUser(String accountNumber, long userId) {
		Account account = accountRepository.findByAccountNumber(accountNumber)
				.orElseThrow(() -> new AccountNotFoundException("accounNumber", accountNumber));

		return account.getUser().getId().equals(userId);
	}

	public BigDecimal getAccountBalance(String accountNumber) {
		Account account = getAccountEntity(accountNumber);
		return account.getBalance();
	}

	public TransactionResponse deposit(DepositRequest request, Long userId) {
		Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
				.orElseThrow(() -> new AccountNotFoundException("accountNumber", request.getAccountNumber()));

		if (!account.getUser().getId().equals(userId)) {
			throw new UnauthorizedAccessException("You don't have access to this account");
		}

		if (account.getStatus() != AccountStatus.ACTIVE) {
			throw new AccountInactiveException(account.getAccountNumber(), account.getStatus().name());
		}

		BigDecimal newBalance = account.getBalance().add(request.getAmount());
		account.setBalance(newBalance);
		accountRepository.save(account);

		Transaction transaction = transactionService.recordTransaction(account, TransactionType.DEPOSIT,
				request.getAmount(), newBalance,
				request.getDescription() != null ? request.getDescription() : "Deposit to " + account.getAccountNumber(),
				null);

		System.out.println(" Deposit completed: " + transaction.getTransactionRef());

		return new TransactionResponse(transaction);
	}

	public TransactionResponse withdraw(WithdrawRequest request, Long userId) {
		Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
				.orElseThrow(() -> new AccountNotFoundException("accountNumber", request.getAccountNumber()));

		if (!account.getUser().getId().equals(userId)) {
			throw new UnauthorizedAccessException("You don't have access to this account");
		}

		if (account.getStatus() != AccountStatus.ACTIVE) {
			throw new AccountInactiveException(account.getAccountNumber(), account.getStatus().name());
		}

		if (account.getBalance().compareTo(request.getAmount()) < 0) {
			throw new InsufficientBalanceException(account.getBalance(), request.getAmount());
		}

		BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
		account.setBalance(newBalance);
		accountRepository.save(account);

		Transaction transaction = transactionService.recordTransaction(account, TransactionType.WITHDRAWAL,
				request.getAmount().negate(), newBalance,
				request.getDescription() != null ? request.getDescription()
						: "Withdrawal from " + account.getAccountNumber(),
				null);

		System.out.println(" Withdrawal completed: " + transaction.getTransactionRef());

		return new TransactionResponse(transaction);
	}

	protected void updateBalance(Account account, BigDecimal newBalance) {
		account.setBalance(newBalance);
		accountRepository.save(account);
	}

	private String generateUniqueAccountNumber() {
		String accountNumber;
		do {
			accountNumber = accountNumberGenerator.generateAccountNumber();
		} while (accountRepository.existsByAccountNumber(accountNumber));

		return accountNumber;
	}
}
