package com.finance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.finance.model.Role;
import com.finance.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	@Query("SELECT u FROM User u WHERE u.username = :username AND u.isDeleted = false")
	Optional<User> findByUsername(@Param("username") String username);
	
	@Query("SELECT u FROM User u WHERE u.isDeleted = false AND u.active = true")
	Page<User> findAllActive(Pageable pageable);
	
	@Query("SELECT u FROM User u WHERE u.isDeleted = false")
	Page<User> findAllNotDeleted(Pageable pageable);
	
	@Query("SELECT u FROM User u WHERE u.isDeleted = false AND u.role = :role")
	Page<User> findByRole(@Param("role") Role role, Pageable pageable);
	
	@Query("SELECT u FROM User u WHERE u.isDeleted = false AND (u.username LIKE %:search% OR u.id = :id)")
	Page<User> searchUsers(@Param("search") String search, @Param("id") Long id, Pageable pageable);
	
	@Query("SELECT u FROM User u WHERE u.isDeleted = false")
	List<User> findAllActive();
	
	long countByIsDeletedFalse();
}
