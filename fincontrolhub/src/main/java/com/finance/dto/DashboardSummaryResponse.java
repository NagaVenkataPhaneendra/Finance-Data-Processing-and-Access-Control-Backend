package com.finance.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
	private Double totalIncome;
	private Double totalExpenses;
	private Double netBalance;
	private Map<String, Double> categoryTotals;
}
