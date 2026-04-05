package com.finance.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialRecordRequest {
	@NotNull(message = "Amount is required")
	@Positive(message = "Amount must be greater than 0")
	private Double amount;
	
	@NotBlank(message = "Type is required")
	@Pattern(regexp = "income|expense", flags = Pattern.Flag.CASE_INSENSITIVE, 
			message = "Type must be either 'income' or 'expense'")
	private String type; // income or expense
	
	@NotBlank(message = "Category is required")
	@Size(min = 2, max = 100, message = "Category must be between 2 and 100 characters")
	private String category;
	
	@NotNull(message = "Date is required")
	private LocalDate date;
	
	@Size(max = 500, message = "Notes cannot exceed 500 characters")
	private String notes;
}
