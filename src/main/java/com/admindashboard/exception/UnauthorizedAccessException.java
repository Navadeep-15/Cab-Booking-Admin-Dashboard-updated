package com.admindashboard.exception;

public class UnauthorizedAccessException extends BaseException {
    public UnauthorizedAccessException(String message, String errorCode) {
        super(message, errorCode);
    }
}