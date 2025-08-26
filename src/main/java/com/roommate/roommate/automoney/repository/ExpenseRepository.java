package com.roommate.roommate.automoney.repository;

import com.roommate.roommate.automoney.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    
    @Query("SELECT e FROM Expense e WHERE e.space.id = :spaceId ORDER BY e.createdAt DESC")
    List<Expense> findBySpaceIdOrderByCreatedAtDesc(@Param("spaceId") Long spaceId);
    
    @Query("SELECT e FROM Expense e WHERE e.space.id = :spaceId AND e.expenseType = :expenseType ORDER BY e.createdAt DESC")
    List<Expense> findBySpaceIdAndExpenseTypeOrderByCreatedAtDesc(@Param("spaceId") Long spaceId, @Param("expenseType") Expense.ExpenseType expenseType);
    
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.space.id = :spaceId")
    BigDecimal getTotalExpenseBySpaceId(@Param("spaceId") Long spaceId);
}
