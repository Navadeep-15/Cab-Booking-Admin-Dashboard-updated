package com.admindashboard.exception;

public class BusinessLogicException extends BaseException {
    public BusinessLogicException(String message, String errorCode) {
        super(message, errorCode);
    }
    
    public BusinessLogicException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION");
    }
}