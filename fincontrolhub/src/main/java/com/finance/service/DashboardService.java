package com.finance.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finance.model.FinancialRecord;
import com.finance.model.User;
import com.finance.repository.FinancialRecordRepository;

@Service
public class DashboardService {
	private FinancialRecordRepository repo;

	@Autowired
	public DashboardService(FinancialRecordRepository repo) {
		this.repo = repo;
	}

	// Global Dashboard Stats (All Users)
	public Double totalIncome() {
		return repo.findAll().stream()
				.filter(r -> !r.isDeleted() && "income".equals(r.getType()))
				.mapToDouble(FinancialRecord::getAmount).sum();
	}

	public Double totalExpenses() {
		return repo.findAll().stream()
				.filter(r -> !r.isDeleted() && "expense".equals(r.getType()))
				.mapToDouble(FinancialRecord::getAmount).sum();
	}

	public Double netBalance() {
		return totalIncome() - totalExpenses();
	}

	public Map<String, Double> categoryTotals() {
		return repo.findAll().stream()
				.filter(r -> !r.isDeleted())
				.collect(Collectors.groupingBy(FinancialRecord::getCategory,
						Collectors.summingDouble(FinancialRecord::getAmount)));
	}

	// User-Specific Dashboard Stats
	public Double userTotalIncome(User user) {
		Double total = repo.sumIncomeByUser(user);
		return total != null ? total : 0.0;
	}

	public Double userTotalExpenses(User user) {
		Double total = repo.sumExpenseByUser(user);
		return total != null ? total : 0.0;
	}

	public Double userNetBalance(User user) {
		return userTotalIncome(user) - userTotalExpenses(user);
	}

	public Map<String, Double> userCategoryTotals(User user) {
		return repo.findByUser(user, org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
				.stream()
				.collect(Collectors.groupingBy(FinancialRecord::getCategory,
						Collectors.summingDouble(FinancialRecord::getAmount)));
	}

	public Map<String, Double> userCategoryExpenseTotals(User user) {
		return repo.findByUserAndType(user, "expense", org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
				.stream()
				.collect(Collectors.groupingBy(FinancialRecord::getCategory,
						Collectors.summingDouble(FinancialRecord::getAmount)));
	}

	public Map<String, Double> userCategoryIncomeTotals(User user) {
		return repo.findByUserAndType(user, "income", org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
				.stream()
				.collect(Collectors.groupingBy(FinancialRecord::getCategory,
						Collectors.summingDouble(FinancialRecord::getAmount)));
	}

	// Recent Activity
	public List<FinancialRecord> getRecentRecords(User user, int limit) {
		return repo.findByUser(user, org.springframework.data.domain.PageRequest.of(0, limit)).getContent();
	}

	// Monthly/Weekly Analysis
	public Map<String, Double> getMonthlyIncome(User user, int months) {
		LocalDate now = LocalDate.now();
		Map<String, Double> monthlyData = new HashMap<>();

		for (int i = months - 1; i >= 0; i--) {
			LocalDate monthStart = now.minusMonths(i).withDayOfMonth(1);
			LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
			String monthKey = monthStart.getYear() + "-" + String.format("%02d", monthStart.getMonthValue());

			List<FinancialRecord> records = repo.findByUserAndDateBetween(user, monthStart, monthEnd,
					org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)).getContent();

			Double monthlyIncome = records.stream()
					.filter(r -> "income".equals(r.getType()) && !r.isDeleted())
					.mapToDouble(FinancialRecord::getAmount).sum();

			monthlyData.put(monthKey, monthlyIncome);
		}
		return monthlyData;
	}

	public Map<String, Double> getMonthlyExpenses(User user, int months) {
		LocalDate now = LocalDate.now();
		Map<String, Double> monthlyData = new HashMap<>();

		for (int i = months - 1; i >= 0; i--) {
			LocalDate monthStart = now.minusMonths(i).withDayOfMonth(1);
			LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
			String monthKey = monthStart.getYear() + "-" + String.format("%02d", monthStart.getMonthValue());

			List<FinancialRecord> records = repo.findByUserAndDateBetween(user, monthStart, monthEnd,
					org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)).getContent();

			Double monthlyExpenses = records.stream()
					.filter(r -> "expense".equals(r.getType()) && !r.isDeleted())
					.mapToDouble(FinancialRecord::getAmount).sum();

			monthlyData.put(monthKey, monthlyExpenses);
		}
		return monthlyData;
	}

	// Comprehensive Dashboard Summary
	public Map<String, Object> getComprehensiveSummary(User user) {
		Map<String, Object> summary = new HashMap<>();
		
		summary.put("totalIncome", userTotalIncome(user));
		summary.put("totalExpenses", userTotalExpenses(user));
		summary.put("netBalance", userNetBalance(user));
		summary.put("categoryTotals", userCategoryTotals(user));
		summary.put("expensesByCategory", userCategoryExpenseTotals(user));
		summary.put("incomeByCategory", userCategoryIncomeTotals(user));
		summary.put("last6Months", new HashMap<String, Object>() {{
			put("income", getMonthlyIncome(user, 6));
			put("expenses", getMonthlyExpenses(user, 6));
		}});
		summary.put("recentTransactions", getRecentRecords(user, 10));
		
		return summary;
	}
}
