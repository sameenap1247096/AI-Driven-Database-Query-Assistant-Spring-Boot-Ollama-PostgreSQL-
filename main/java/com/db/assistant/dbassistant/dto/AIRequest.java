package com.db.assistant.dbassistant.dto;

import lombok.Data;

@Data
public class AIRequest {
    private String action;
    private String transactionId;
    private Double amount;
    private String status;
    private String narration;
    private Integer limit;
}
