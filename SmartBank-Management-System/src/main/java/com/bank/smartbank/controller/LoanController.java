package com.bank.smartbank.controller;

import com.bank.smartbank.dto.common.ApiResponse;
import com.bank.smartbank.dto.loan.LoanRequest;
import com.bank.smartbank.dto.loan.LoanResponse;
import com.bank.smartbank.dto.loan.RepaymentRequest;
import com.bank.smartbank.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "*")
public class LoanController {

	private final LoanService loanService;
	private final CurrentUser currentUser;

	public LoanController(LoanService loanService, CurrentUser currentUser) {
		this.loanService = loanService;
		this.currentUser = currentUser;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<LoanResponse>> applyForLoan(@Valid @RequestBody LoanRequest request) {

		Long userId = currentUser.getUserId();

		LoanResponse response = loanService.applyForLoan(userId, request);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Loan application submitted successfully", response));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<LoanResponse>>> getMyLoans() {
		Long userId = currentUser.getUserId();

		List<LoanResponse> loans = loanService.getUserLoans(userId);

		return ResponseEntity.ok(ApiResponse.success("Your loans retrieved successfully", loans));
	}

	@GetMapping("/{loanId}")
	public ResponseEntity<ApiResponse<LoanResponse>> getLoan(@PathVariable Long loanId) {

		Long userId = currentUser.getUserId();

		if (!loanService.isLoanOwnedByUser(loanId, userId)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error("You don't have access to this loan"));
		}

		LoanResponse loan = loanService.getLoanById(loanId);
		return ResponseEntity.ok(ApiResponse.success("Loan details retrieved", loan));
	}

	@GetMapping("/number/{loanNumber}")
	public ResponseEntity<ApiResponse<LoanResponse>> getLoanByNumber(@PathVariable String loanNumber) {

		Long userId = currentUser.getUserId();

		if (!loanService.isLoanOwnedByUser(loanNumber, userId)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error("You don't have access to this loan"));
		}

		LoanResponse loan = loanService.getLoanByNumber(loanNumber);

		return ResponseEntity.ok(ApiResponse.success("Loan details retrieved", loan));
	}

	@PostMapping("/{loanId}/repay")
	public ResponseEntity<ApiResponse<LoanResponse>> repayLoan(@PathVariable Long loanId,
			@Valid @RequestBody RepaymentRequest request) {

		Long userId = currentUser.getUserId();

		if (!loanService.isLoanOwnedByUser(loanId, userId)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(ApiResponse.error("You don't have access to this loan"));
		}

		LoanResponse loan = loanService.repayLoan(loanId, userId, request);

		return ResponseEntity.ok(ApiResponse.success("Repayment completed successfully", loan));
	}

}
