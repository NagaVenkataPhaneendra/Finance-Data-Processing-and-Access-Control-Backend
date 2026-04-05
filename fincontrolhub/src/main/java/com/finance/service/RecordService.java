package com.finance.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.finance.dto.PaginatedResponse;
import com.finance.model.FinancialRecord;
import com.finance.model.User;
import com.finance.repository.FinancialRecordRepository;

@Service
public class RecordService {
	private FinancialRecordRepository repo;

	@Autowired
	public RecordService(FinancialRecordRepository repo) {
		this.repo = repo;
	}

	public FinancialRecord create(FinancialRecord record) {
		if (record.getAmount() == null || record.getAmount() <= 0) {
			throw new IllegalArgumentException("Amount must be greater than 0");
		}
		if (record.getDate() == null) {
			throw new IllegalArgumentException("Date is required");
		}
		record.setDeleted(false);
		return repo.save(record);
	}

	public List<FinancialRecord> listAll() {
		return repo.findAll();
	}

	public Optional<FinancialRecord> findById(Long id) {
		return repo.findById(id)
				.filter(r -> !r.isDeleted());
	}

	public FinancialRecord update(Long id, FinancialRecord updated, User user) {
		FinancialRecord record = repo.findByUserAndId(user, id)
				.orElseThrow(() -> new RuntimeException("Record not found"));
		
		if (updated.getAmount() != null && updated.getAmount() > 0) {
			record.setAmount(updated.getAmount());
		}
		if (updated.getCategory() != null && !updated.getCategory().isEmpty()) {
			record.setCategory(updated.getCategory());
		}
		if (updated.getDate() != null) {
			record.setDate(updated.getDate());
		}
		if (updated.getNotes() != null) {
			record.setNotes(updated.getNotes());
		}
		if (updated.getType() != null && !updated.getType().isEmpty()) {
			record.setType(updated.getType());
		}
		return repo.save(record);
	}

	public void delete(Long id, User user) {
		FinancialRecord record = repo.findByUserAndId(user, id)
				.orElseThrow(() -> new RuntimeException("Record not found"));
		record.setDeleted(true);
		repo.save(record);
	}

	public void permanentDelete(Long id) {
		repo.deleteById(id);
	}

	public PaginatedResponse<FinancialRecord> findByUserPaginated(User user, Pageable pageable) {
		Page<FinancialRecord> page = repo.findByUser(user, pageable);
		return buildPaginatedResponse(page);
	}

	public PaginatedResponse<FinancialRecord> findByUserAndType(User user, String type, Pageable pageable) {
		Page<FinancialRecord> page = repo.findByUserAndType(user, type, pageable);
		return buildPaginatedResponse(page);
	}

	public PaginatedResponse<FinancialRecord> findByUserAndCategory(User user, String category, Pageable pageable) {
		Page<FinancialRecord> page = repo.findByUserAndCategory(user, category, pageable);
		return buildPaginatedResponse(page);
	}

	public PaginatedResponse<FinancialRecord> findByUserAndDateRange(User user, LocalDate start, LocalDate end, Pageable pageable) {
		if (start.isAfter(end)) {
			throw new IllegalArgumentException("Start date must be before end date");
		}
		Page<FinancialRecord> page = repo.findByUserAndDateBetween(user, start, end, pageable);
		return buildPaginatedResponse(page);
	}

	public PaginatedResponse<FinancialRecord> searchRecords(User user, String keyword, Pageable pageable) {
		Page<FinancialRecord> page = repo.searchByUserAndKeyword(user, keyword, pageable);
		return buildPaginatedResponse(page);
	}

	public Double getTotalIncome(User user) {
		Double total = repo.sumIncomeByUser(user);
		return total != null ? total : 0.0;
	}

	public Double getTotalExpense(User user) {
		Double total = repo.sumExpenseByUser(user);
		return total != null ? total : 0.0;
	}

	public Double getCategoryTotal(User user, String type, String category) {
		Double total = repo.sumByUserTypeAndCategory(user, type, category);
		return total != null ? total : 0.0;
	}

	private <T> PaginatedResponse<T> buildPaginatedResponse(Page<T> page) {
		return new PaginatedResponse<>(
				page.getContent(),
				page.getNumber(),
				page.getSize(),
				page.getTotalElements(),
				page.getTotalPages(),
				page.isFirst(),
				page.isLast(),
				page.hasNext(),
				page.hasPrevious()
		);
	}
}
