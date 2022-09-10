package com.mpusinhol.imageservice.exception;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String message) {
        super(message);
    }

    public ObjectNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
