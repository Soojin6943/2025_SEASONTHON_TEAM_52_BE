package com.roommate.roommate.settlement.repository;

import com.roommate.roommate.settlement.entity.Expense;
import com.roommate.roommate.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    

    
    // 정산별 지출 조회
    @Query("SELECT e FROM Expense e WHERE e.settlement.id = :settlementId ORDER BY e.createdAt ASC")
    List<Expense> findBySettlementIdOrderByCreatedAtAsc(@Param("settlementId") Long settlementId);
    
    // 정산별 총 지출 금액
    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.settlement.id = :settlementId")
    BigDecimal getTotalExpenseBySettlementId(@Param("settlementId") Long settlementId);
    
    // 정산과 작성자별 지출 조회
    @Query("SELECT e FROM Expense e WHERE e.settlement = :settlement AND e.createdBy = :userId")
    List<Expense> findBySettlementAndCreatedBy(@Param("settlement") Settlement settlement, @Param("userId") Long userId);
}
