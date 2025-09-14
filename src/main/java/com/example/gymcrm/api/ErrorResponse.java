package com.example.gymcrm.api;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

public class ErrorResponse {
    public String error;
    public String code;
    public String path;
    public String txId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public OffsetDateTime timestamp = OffsetDateTime.now();

    public ErrorResponse() {}

    public ErrorResponse(String error, String code, String path, String txId) {
        this.error = error;
        this.code = code;
        this.path = path;
        this.txId = txId;
    }
}
