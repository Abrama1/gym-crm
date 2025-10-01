package com.example.gymcrm.dto;

import java.time.OffsetDateTime;

public class ErrorResponse {
    private String txId;
    private String code;
    private String message;
    private OffsetDateTime timestamp = OffsetDateTime.now();

    public ErrorResponse() {}

    public ErrorResponse(String txId, String code, String message) {
        this.txId = txId;
        this.code = code;
        this.message = message;
    }

    public String getTxId() { return txId; }
    public void setTxId(String txId) { this.txId = txId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
}
