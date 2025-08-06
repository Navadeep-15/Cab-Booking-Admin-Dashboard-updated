package com.admindashboard.exception;

public class DatabaseException extends BaseException {
    public DatabaseException(String message, String errorCode) {
        super(message, errorCode);
    }
}