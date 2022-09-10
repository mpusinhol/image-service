package com.mpusinhol.imageservice.exception;

public class InvalidFilenameException extends RuntimeException {

    public InvalidFilenameException(String message) {
        super(message);
    }

    public InvalidFilenameException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
