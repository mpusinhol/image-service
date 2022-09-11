package com.mpusinhol.imageservice.exception;

public class InvalidImagePreDefinedTypeException extends RuntimeException {

    public InvalidImagePreDefinedTypeException(String message) {
        super(message);
    }

    public InvalidImagePreDefinedTypeException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
