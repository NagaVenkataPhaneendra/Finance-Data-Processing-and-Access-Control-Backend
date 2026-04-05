package com.finance.dto;

import com.finance.model.Role;
import com.finance.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
	private Long id;
	private String username;
	private Role role;
	private boolean active;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	// constructor for convenience
	public UserResponse(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.role = user.getRole();
		this.active = user.isActive();
		this.createdAt = user.getCreatedAt();
		this.updatedAt = user.getUpdatedAt();
	}
}
