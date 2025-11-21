package com.db.assistant.dbassistant.service;


import com.db.assistant.dbassistant.dto.AIRequest;
import com.db.assistant.dbassistant.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class IntentHandlerService {

    @Autowired
    private TransactionRepository transactionRepository;

    public String handleIntent(AIRequest intent) {
        String action = intent.getAction();
        if ("get_status".equals(action)) {

            String txnId = intent.getTransactionId();

            if (txnId == null || txnId.isEmpty())
                return "transactionId missing";

            return transactionRepository.findStatusByTransactionId(txnId)
                    .map(info -> Map.of(
                            "Transaction ID ", txnId,
                            "Status ", info.getStatus(),
                            "Narration ", info.getNarration()
                    )).orElse(Map.of("error", "Transaction not found")).toString();

        } else if ("count_failed".equals(action)) {
            int cnt = transactionRepository.countByStatus("Failed");
            return "Failed transactions count: " + cnt;

        } else if ("list_transactions".equals(action)) {
            Integer limit = intent.getLimit();

            if (limit == null)
                limit = 10;

            List<Map<String,Object>> rows = transactionRepository.findRecentTransactions(limit);

            if (rows.isEmpty())
                return "No transactions found";

            StringBuilder out = new StringBuilder();
            rows.forEach(r -> out.append(r.toString()).append("\n"));

            return out.toString();
        } else {
            return "Unknown action: " + action;
        }
    }
}
