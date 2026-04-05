package com.finance.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.finance.dto.PaginatedResponse;
import com.finance.dto.RegisterRequest;
import com.finance.dto.UserResponse;
import com.finance.model.User;
import com.finance.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "User administration endpoints (ADMIN only)")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Create a new user", description = "Create a new user with specified role (ADMIN only)")
	public ResponseEntity<?> createUser(@RequestBody RegisterRequest request) {
		try {
			User user = userService.register(request.getUsername(), request.getPassword(), request.getRole());
			return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(user));
		} catch (IllegalArgumentException e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Invalid request");
			error.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get all users", description = "Retrieve paginated list of all users")
	public ResponseEntity<?> getAllUsers(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size);
			PaginatedResponse<User> response = userService.findAllPaginated(pageable);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Fetch failed");
			error.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@GetMapping("/active")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get active users", description = "Retrieve paginated list of active users only")
	public ResponseEntity<?> getActiveUsers(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size);
			PaginatedResponse<User> response = userService.findAllActivePaginated(pageable);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Fetch failed");
			error.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@GetMapping("/search")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Search users", description = "Search users by username or ID")
	public ResponseEntity<?> searchUsers(
			@RequestParam String q,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		try {
			Pageable pageable = PageRequest.of(page, size);
			PaginatedResponse<User> response = userService.searchUsers(q, pageable);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Search failed");
			error.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
	public ResponseEntity<?> getUser(@PathVariable Long id) {
		try {
			User user = userService.findById(id)
					.orElseThrow(() -> new RuntimeException("User not found"));
			return ResponseEntity.ok(new UserResponse(user));
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "User not found");
			error.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Update user", description = "Update user role or other details")
	public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userUpdate) {
		try {
			User user = userService.updateUser(id, userUpdate);
			return ResponseEntity.ok(new UserResponse(user));
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Update failed");
			error.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}

	@PutMapping("/{id}/status")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Update user status", description = "Activate or deactivate a user")
	public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam boolean active) {
		try {
			User user = userService.updateStatus(id, active);
			return ResponseEntity.ok(new UserResponse(user));
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Status update failed");
			error.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Delete user", description = "Soft delete a user (logical removal)")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		try {
			userService.delete(id);
			Map<String, String> response = new HashMap<>();
			response.put("message", "User deleted successfully");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Delete failed");
			error.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}

	@DeleteMapping("/{id}/permanent")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Permanently delete user", description = "Hard delete a user (permanent removal)")
	public ResponseEntity<?> permanentlyDeleteUser(@PathVariable Long id) {
		try {
			userService.permanentDelete(id);
			Map<String, String> response = new HashMap<>();
			response.put("message", "User permanently deleted");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Permanent delete failed");
			error.put("message", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
	}

	@GetMapping("/stats/total")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get user statistics", description = "Get total active users count")
	public ResponseEntity<?> getUserStats() {
		try {
			Map<String, Object> stats = new HashMap<>();
			stats.put("totalActiveUsers", userService.getTotalActiveUsers());
			return ResponseEntity.ok(stats);
		} catch (Exception e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Stats fetch failed");
			error.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}
}
