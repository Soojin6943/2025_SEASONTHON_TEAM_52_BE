package com.roommate.roommate.settlement.repository;

import com.roommate.roommate.settlement.entity.SettlementExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementExpenseRepository extends JpaRepository<SettlementExpense, Long> {
    
    @Query("SELECT se FROM SettlementExpense se WHERE se.settlement.id = :settlementId ORDER BY se.createdAt ASC")
    List<SettlementExpense> findBySettlementIdOrderByCreatedAtAsc(@Param("settlementId") Long settlementId);
    
    @Query("SELECT se FROM SettlementExpense se WHERE se.expense.id = :expenseId ORDER BY se.createdAt DESC")
    List<SettlementExpense> findByExpenseIdOrderByCreatedAtDesc(@Param("expenseId") Long expenseId);
    
    @Query("SELECT se FROM SettlementExpense se WHERE se.settlement.space.id = :spaceId ORDER BY se.createdAt DESC")
    List<SettlementExpense> findBySpaceIdOrderByCreatedAtDesc(@Param("spaceId") Long spaceId);
}
