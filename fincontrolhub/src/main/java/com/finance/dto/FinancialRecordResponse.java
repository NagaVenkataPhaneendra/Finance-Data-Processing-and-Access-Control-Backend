package com.finance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.finance.model.FinancialRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialRecordResponse {
	private Long id;
	private Double amount;
	private String type;
	private String category;
	private LocalDate date;
	private String notes;
	private String username;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public FinancialRecordResponse(FinancialRecord record) {
		this.id = record.getId();
		this.amount = record.getAmount();
		this.type = record.getType();
		this.category = record.getCategory();
		this.date = record.getDate();
		this.notes = record.getNotes();
		this.username = record.getUser().getUsername();
		this.createdAt = record.getCreatedAt();
		this.updatedAt = record.getUpdatedAt();
	}
}
