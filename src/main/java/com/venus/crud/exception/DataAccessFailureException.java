package com.venus.crud.exception;

public class DataAccessFailureException extends RuntimeException {

    public DataAccessFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
