package com.db.assistant.dbassistant.entity;

import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class TransactionStatusInfo {
    private String status;
    private String narration;

    public TransactionStatusInfo(String status, String narration) {
        this.status = status;
        this.narration = narration;
    }

}
