package com.db.assistant.dbassistant.service;

import com.db.assistant.dbassistant.entity.Transaction;
import com.db.assistant.dbassistant.entity.TransactionStatusInfo;
import com.db.assistant.dbassistant.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepo;

    public Optional<TransactionStatusInfo> getTransactionStatus(String txnId){
        return transactionRepo.findStatusByTransactionId(txnId);
    }
}
