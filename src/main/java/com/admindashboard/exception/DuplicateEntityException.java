package com.admindashboard.exception;

public class DuplicateEntityException extends BusinessLogicException {
    
    // Standard constructor (recommended)
    public DuplicateEntityException(String entityName, String fieldName, Object value) {
        super(
            String.format("%s with %s '%s' already exists", entityName, fieldName, value),
            String.format("%s_%s_EXISTS", 
                entityName.toUpperCase(), 
                fieldName.toUpperCase().replace(" ", "_"))
        );
    }
    
    // Legacy support constructor
    public DuplicateEntityException(String message, String errorCode) {
        super(message, errorCode);
    }
}