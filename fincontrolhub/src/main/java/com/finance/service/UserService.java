package com.finance.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.finance.dto.PaginatedResponse;
import com.finance.model.Role;
import com.finance.model.User;
import com.finance.repository.UserRepository;

@Service
public class UserService {
	private UserRepository repo;
	private PasswordEncoder encoder;

	@Autowired
	public UserService(UserRepository repo, PasswordEncoder encoder) {
		this.repo = repo;
		this.encoder = encoder;
	}

	public User register(String username, String password, Role role) {
		if (repo.findByUsername(username).isPresent()) {
			throw new IllegalArgumentException("Username already exists");
		}
		User user = new User();
		user.setUsername(username);
		user.setPassword(encoder.encode(password));
		user.setRole(role);
		user.setActive(true);
		user.setDeleted(false);
		return repo.save(user);
	}

	public Optional<User> findByUsername(String username) {
		return repo.findByUsername(username);
	}

	public List<User> findAll() {
		return repo.findAllActive();
	}

	public Optional<User> findById(Long id) {
		return repo.findById(id)
				.filter(u -> !u.isDeleted());
	}

	public User updateStatus(Long id, boolean active) {
		User user = repo.findById(id)
				.filter(u -> !u.isDeleted())
				.orElseThrow(() -> new RuntimeException("User not found"));
		user.setActive(active);
		return repo.save(user);
	}

	public User updateUser(Long id, User userDetails) {
		User user = repo.findById(id)
				.filter(u -> !u.isDeleted())
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		if (userDetails.getUsername() != null && !userDetails.getUsername().isEmpty()) {
			user.setUsername(userDetails.getUsername());
		}
		if (userDetails.getRole() != null) {
			user.setRole(userDetails.getRole());
		}
		if (userDetails.isActive() != user.isActive()) {
			user.setActive(userDetails.isActive());
		}
		return repo.save(user);
	}

	public void delete(Long id) {
		User user = repo.findById(id)
				.filter(u -> !u.isDeleted())
				.orElseThrow(() -> new RuntimeException("User not found"));
		user.setDeleted(true);
		repo.save(user);
	}

	public void permanentDelete(Long id) {
		repo.deleteById(id);
	}

	public PaginatedResponse<User> findAllPaginated(Pageable pageable) {
		Page<User> page = repo.findAllNotDeleted(pageable);
		return buildPaginatedResponse(page);
	}

	public PaginatedResponse<User> findAllActivePaginated(Pageable pageable) {
		Page<User> page = repo.findAllActive(pageable);
		return buildPaginatedResponse(page);
	}

	public PaginatedResponse<User> findByRolePaginated(Role role, Pageable pageable) {
		Page<User> page = repo.findByRole(role, pageable);
		return buildPaginatedResponse(page);
	}

	public PaginatedResponse<User> searchUsers(String search, Pageable pageable) {
		try {
			Long id = Long.parseLong(search);
			Page<User> page = repo.searchUsers(search, id, pageable);
			return buildPaginatedResponse(page);
		} catch (NumberFormatException e) {
			Page<User> page = repo.searchUsers(search, 0L, pageable);
			return buildPaginatedResponse(page);
		}
	}

	public User replaceUser(Long id, User userDetails) {
		User user = repo.findById(id)
				.filter(u -> !u.isDeleted())
				.orElseThrow(() -> new RuntimeException("User not found"));
		
		if (userDetails.getRole() != null) {
			user.setRole(userDetails.getRole());
		}
		if (userDetails.isActive() != user.isActive()) {
			user.setActive(userDetails.isActive());
		}
		return repo.save(user);
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

	public long getTotalActiveUsers() {
		return repo.countByIsDeletedFalse();
	}
}
