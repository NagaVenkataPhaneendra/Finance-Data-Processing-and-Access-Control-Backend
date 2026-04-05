package com.finance.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finance.dto.FinancialRecordRequest;
import com.finance.dto.FinancialRecordResponse;
import com.finance.dto.PaginatedResponse;
import com.finance.model.FinancialRecord;
import com.finance.model.User;
import com.finance.service.RecordService;
import com.finance.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/records")
@Tag(name = "Financial Records", description = "Operations on financial records (income/expense)")
public class RecordController {
	@Autowired
	private RecordService service;
	
	@Autowired
	private UserService userService;

	@PostMapping
	@PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
	@Operation(summary = "Create a new financial record", description = "Create income or expense record for authenticated user")
	public ResponseEntity<?> create(@Valid @RequestBody FinancialRecordRequest req, Authentication auth) {
		try {
			User user = userService.findByUsername(auth.getName())
					.orElseThrow(() -> new RuntimeException("User not found"));
			
			FinancialRecord record = new FinancialRecord();
			record.setAmount(req.getAmount());
			record.setType(req.getType().toLowerCase());
			record.setCategory(req.getCategory());
			record.setDate(req.getDate());
			record.setNotes(req.getNotes());
			record.setUser(user);
			
			FinancialRecord created = service.create(record);
			return ResponseEntity.status(HttpStatus.CREATED).body(new FinancialRecordResponse(created));
		} catch (IllegalArgumentException e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Invalid request");
			error.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
	@Operation(summary = "Get user's financial records", description = "Retrieve paginated list of user's financial records")
	public ResponseEntity<?> list(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "date,desc") String sort,
			Authentication auth) {
		try {
			User user = userService.findByUsername(auth.getName())
					.orElseThrow(() -> new RuntimeException("User not found"));
			
			String[] sortParts = sort.split(",");
			Sort.Direction direction = sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1]) 
					? Sort.Direction.ASC : Sort.Direction.DESC;
			
			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));
			PaginatedResponse<FinancialRecord> response = service.findByUserPaginated(user, pageable);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Fetch failed");
			error.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}

	@GetMapping("/filter")
	@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
	@Operation(summary = "Filter records by type", description = "Filter records by type (income or expense)")
	public ResponseEntity<?> filterByType(
			@RequestParam String type,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			Authentication auth) {
		try {
			User user = userService.findByUsername(auth.getName())
					.orElseThrow(() -> new RuntimeException("User not found"));
			
			Pageable pageable = PageRequest.of(page, size);
			PaginatedResponse<FinancialRecord> response = service.findByUserAndType(user, type.toLowerCase(), pageable);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Fetch failed");
			error.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@GetMapping("/category")
	@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
	@Operation(summary = "Filter records by category", description = "Filter records by category name")
	public ResponseEntity<?> filterByCategory(
			@RequestParam String category,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			Authentication auth) {
		try {
			User user = userService.findByUsername(auth.getName())
					.orElseThrow(() -> new RuntimeException("User not found"));
			
			Pageable pageable = PageRequest.of(page, size);
			PaginatedResponse<FinancialRecord> response = service.findByUserAndCategory(user, category, pageable);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Fetch failed");
			error.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@GetMapping("/date-range")
	@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
	@Operation(summary = "Filter records by date range", description = "Filter records between two dates")
	public ResponseEntity<?> filterByDateRange(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			Authentication auth) {
		try {
			User user = userService.findByUsername(auth.getName())
					.orElseThrow(() -> new RuntimeException("User not found"));
			
			Pageable pageable = PageRequest.of(page, size);
			PaginatedResponse<FinancialRecord> response = service.findByUserAndDateRange(user, start, end, pageable);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Fetch failed");
			error.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@GetMapping("/search")
	@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
	@Operation(summary = "Search records", description = "Search records by keyword (category or notes)")
	public ResponseEntity<?> search(
			@RequestParam String q,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			Authentication auth) {
		try {
			User user = userService.findByUsername(auth.getName())
					.orElseThrow(() -> new RuntimeException("User not found"));
			
			Pageable pageable = PageRequest.of(page, size);
			PaginatedResponse<FinancialRecord> response = service.searchRecords(user, q, pageable);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Search failed");
			error.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
	@Operation(summary = "Get record by ID", description = "Retrieve a specific financial record")
	public ResponseEntity<?> getById(@PathVariable Long id, Authentication auth) {
		try {
			User user = userService.findByUsername(auth.getName())
					.orElseThrow(() -> new RuntimeException("User not found"));
			
			FinancialRecord record = service.findById(id)
					.orElseThrow(() -> new RuntimeException("Record not found"));
			
			// Verify ownership (ANALYST can only see own, ADMIN can see all)
			if (!auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")) 
					&& !record.getUser().getId().equals(user.getId())) {
				Map<String, Object> error = new HashMap<>();
				error.put("error", "Access denied");
				error.put("message", "Cannot access other user's records");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
			}
			
			return ResponseEntity.ok(new FinancialRecordResponse(record));
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Fetch failed");
			error.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
	@Operation(summary = "Update financial record", description = "Update an existing financial record")
	public ResponseEntity<?> update(
			@PathVariable Long id,
			@Valid @RequestBody FinancialRecordRequest req,
			Authentication auth) {
		try {
			User user = userService.findByUsername(auth.getName())
					.orElseThrow(() -> new RuntimeException("User not found"));
			
			FinancialRecord existingRecord = service.findById(id)
					.orElseThrow(() -> new RuntimeException("Record not found"));
			
			// Verify ownership
			if (!existingRecord.getUser().getId().equals(user.getId())) {
				Map<String, Object> error = new HashMap<>();
				error.put("error", "Access denied");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
			}
			
			FinancialRecord updated = new FinancialRecord();
			updated.setAmount(req.getAmount());
			updated.setType(req.getType().toLowerCase());
			updated.setCategory(req.getCategory());
			updated.setDate(req.getDate());
			updated.setNotes(req.getNotes());
			
			FinancialRecord result = service.update(id, updated, user);
			return ResponseEntity.ok(new FinancialRecordResponse(result));
		} catch (IllegalArgumentException e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Invalid request");
			error.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Update failed");
			error.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
	@Operation(summary = "Delete financial record", description = "Soft delete (logical removal) of a financial record")
	public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
		try {
			User user = userService.findByUsername(auth.getName())
					.orElseThrow(() -> new RuntimeException("User not found"));
			
			FinancialRecord record = service.findById(id)
					.orElseThrow(() -> new RuntimeException("Record not found"));
			
			// Verify ownership
			if (!record.getUser().getId().equals(user.getId())) {
				Map<String, Object> error = new HashMap<>();
				error.put("error", "Access denied");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
			}
			
			service.delete(id, user);
			Map<String, String> response = new HashMap<>();
			response.put("message", "Record deleted successfully");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Delete failed");
			error.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}
}
