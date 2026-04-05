package com.finance.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.finance.model.FinancialRecord;
import com.finance.model.User;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {
	
	@Query("SELECT f FROM FinancialRecord f WHERE f.isDeleted = false AND f.user = :user ORDER BY f.date DESC")
	Page<FinancialRecord> findByUser(@Param("user") User user, Pageable pageable);
	
	@Query("SELECT f FROM FinancialRecord f WHERE f.isDeleted = false AND f.user = :user AND f.type = :type")
	Page<FinancialRecord> findByUserAndType(@Param("user") User user, @Param("type") String type, Pageable pageable);

	@Query("SELECT f FROM FinancialRecord f WHERE f.isDeleted = false AND f.user = :user AND f.category = :category")
	Page<FinancialRecord> findByUserAndCategory(@Param("user") User user, @Param("category") String category, Pageable pageable);

	@Query("SELECT f FROM FinancialRecord f WHERE f.isDeleted = false AND f.user = :user AND f.date BETWEEN :start AND :end")
	Page<FinancialRecord> findByUserAndDateBetween(@Param("user") User user, @Param("start") LocalDate start, @Param("end") LocalDate end, Pageable pageable);
	
	@Query("SELECT f FROM FinancialRecord f WHERE f.isDeleted = false AND f.user = :user AND (f.category LIKE %:search% OR f.notes LIKE %:search%)")
	Page<FinancialRecord> searchByUserAndKeyword(@Param("user") User user, @Param("search") String search, Pageable pageable);
	
	@Query("SELECT f FROM FinancialRecord f WHERE f.isDeleted = false AND f.user = :user AND f.id = :id")
	Optional<FinancialRecord> findByUserAndId(@Param("user") User user, @Param("id") Long id);
	
	@Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.isDeleted = false AND f.user = :user AND f.type = 'income'")
	Double sumIncomeByUser(@Param("user") User user);
	
	@Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.isDeleted = false AND f.user = :user AND f.type = 'expense'")
	Double sumExpenseByUser(@Param("user") User user);
	
	@Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.isDeleted = false AND f.user = :user AND f.type = :type AND f.category = :category")
	Double sumByUserTypeAndCategory(@Param("user") User user, @Param("type") String type, @Param("category") String category);
}
