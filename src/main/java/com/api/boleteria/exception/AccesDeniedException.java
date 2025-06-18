package com.api.boleteria.exception;

public class AccesDeniedException extends RuntimeException {
    public AccesDeniedException(String message) {
        super(message);
    }
}
