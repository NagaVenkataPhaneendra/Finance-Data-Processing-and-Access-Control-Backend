package com.finance.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.finance.config.JwtUtil;
import com.finance.dto.LoginRequest;
import com.finance.dto.RegisterRequest;
import com.finance.dto.UserResponse;
import com.finance.model.User;
import com.finance.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
public class AuthController {
	@Autowired
	private UserService userService;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private AuthenticationManager authManager;

	@PostMapping("/register")
	@Operation(summary = "Register a new user", description = "Create a new user account with username, password, and role")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
		try {
			User user = userService.register(req.getUsername(), req.getPassword(), req.getRole());
			Map<String, Object> response = new HashMap<>();
			response.put("message", "User registered successfully");
			response.put("user", new UserResponse(user));
			return ResponseEntity.ok(response);
		} catch (IllegalArgumentException e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Registration failed");
			error.put("message", e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}

	@PostMapping("/login")
	@Operation(summary = "User login", description = "Authenticate user and return JWT token")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
		try {
			authManager.authenticate(
					new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
			String token = jwtUtil.generateToken(req.getUsername());
			User user = userService.findByUsername(req.getUsername())
					.orElseThrow(() -> new RuntimeException("User not found"));
			
			Map<String, Object> response = new HashMap<>();
			response.put("token", token);
			response.put("user", new UserResponse(user));
			response.put("expiresIn", 86400);
			response.put("tokenType", "Bearer");
			return ResponseEntity.ok(response);
		} catch (AuthenticationException e) {
			Map<String, Object> error = new HashMap<>();
			error.put("error", "Authentication failed");
			error.put("message", "Invalid username or password");
			return ResponseEntity.status(401).body(error);
		}
	}
}