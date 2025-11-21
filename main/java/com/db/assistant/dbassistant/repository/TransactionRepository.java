package com.db.assistant.dbassistant.repository;

import com.db.assistant.dbassistant.entity.Transaction;
import com.db.assistant.dbassistant.entity.TransactionStatusInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    @Query(
            value = "SELECT status, narration FROM transactions WHERE transaction_id = :txnId",
            nativeQuery = true
    )
    Optional<TransactionStatusInfo> findStatusByTransactionId(@Param("txnId") String txnId);


    @Query(
            value = "SELECT COUNT(*) FROM transactions WHERE status = :status",
            nativeQuery = true
    )
    int countByStatus(@Param("status") String status);

    @Query(value = "SELECT transaction_id, status, amount , narration FROM transactions ORDER BY id DESC LIMIT :limit", nativeQuery = true)
    List<Map<String, Object>> findRecentTransactions(@Param("limit") int limit);

}
