package com.admindashboard.exception;

public class EntityNotFoundException extends BaseException {
    
    // Constructor with entity name and ID (recommended approach)
    public EntityNotFoundException(String entityName, Long id) {
        super(String.format("%s not found with ID: %d", entityName, id), 
              String.format("%s_001", entityName.toUpperCase()));
    }
    
    // Constructor with custom message and error code
    public EntityNotFoundException(String message, String errorCode) {
        super(message, errorCode);
    }
}