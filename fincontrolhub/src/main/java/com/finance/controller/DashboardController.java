package com.finance.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finance.model.User;
import com.finance.service.DashboardService;
import com.finance.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Financial dashboard and analytics")
public class DashboardController {
	@Autowired
	private DashboardService service;
	
	@Autowired
	private UserService userService;

	@GetMapping("/summary")
	@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
	@Operation(summary = "Get comprehensive dashboard summary", 
	           description = "Returns total income, expenses, net balance, category breakdown, monthly trends, and recent transactions")
	public ResponseEntity<?> summary(Authentication auth) {
		try {
			User user = userService.findByUsername(auth.getName())
					.orElseThrow(() -> new RuntimeException("User not found"));
			
			Map<String, Object> response = new HashMap<>(service.getComprehensiveSummary(user));
			response.put("currency", "USD");
			response.put("period", "All Time");
			response.put("lastUpdated", System.currentTimeMillis());
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Dashboard fetch failed");
			error.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@GetMapping("/income")
	@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
	@Operation(summary = "Get income summary", 
	           description = "Returns total income and monthly income breakdown")
	public ResponseEntity<?> getIncome(
			@RequestParam(defaultValue = "6") int months,
			Authentication auth) {
		try {
			User user = userService.findByUsername(auth.getName())
					.orElseThrow(() -> new RuntimeException("User not found"));
			
			Map<String, Object> response = new HashMap<>();
			response.put("totalIncome", service.userTotalIncome(user));
			response.put("monthly", service.getMonthlyIncome(user, months));
			response.put("periodMonths", months);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Fetch failed");
			error.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@GetMapping("/expenses")
	@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
	@Operation(summary = "Get expense summary", 
	           description = "Returns total expenses and monthly expense breakdown")
	public ResponseEntity<?> getExpenses(
			@RequestParam(defaultValue = "6") int months,
			Authentication auth) {
		try {
			User user = userService.findByUsername(auth.getName())
					.orElseThrow(() -> new RuntimeException("User not found"));
			
			Map<String, Object> response = new HashMap<>();
			response.put("totalExpenses", service.userTotalExpenses(user));
			response.put("monthly", service.getMonthlyExpenses(user, months));
			response.put("periodMonths", months);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Fetch failed");
			error.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@GetMapping("/balance")
	@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
	@Operation(summary = "Get net balance", 
	           description = "Returns net balance (income - expenses)")
	public ResponseEntity<?> getBalance(Authentication auth) {
		try {
			User user = userService.findByUsername(auth.getName())
					.orElseThrow(() -> new RuntimeException("User not found"));
			
			Map<String, Object> response = new HashMap<>();
			response.put("netBalance", service.userNetBalance(user));
			response.put("totalIncome", service.userTotalIncome(user));
			response.put("totalExpenses", service.userTotalExpenses(user));
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Fetch failed");
			error.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@GetMapping("/categories")
	@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
	@Operation(summary = "Get category breakdown", 
	           description = "Returns totals grouped by category")
	public ResponseEntity<?> getCategories(Authentication auth) {
		try {
			User user = userService.findByUsername(auth.getName())
					.orElseThrow(() -> new RuntimeException("User not found"));
			
			Map<String, Object> response = new HashMap<>();
			response.put("totalByCategory", service.userCategoryTotals(user));
			response.put("expensesByCategory", service.userCategoryExpenseTotals(user));
			response.put("incomeByCategory", service.userCategoryIncomeTotals(user));
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Fetch failed");
			error.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}
}
