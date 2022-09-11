package com.mpusinhol.imageservice.resource.exception;

import com.mpusinhol.imageservice.dto.StandardError;
import com.mpusinhol.imageservice.exception.InternalServerErrorException;
import com.mpusinhol.imageservice.exception.InvalidImagePreDefinedTypeException;
import com.mpusinhol.imageservice.exception.ObjectNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Instant;

@ControllerAdvice
@Slf4j
public class ResourceExceptionHandler {

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<StandardError> objectNotFound(ObjectNotFoundException exception, HttpServletRequest request) {
        log.info(exception.getMessage());
        StandardError error = new StandardError(HttpStatus.NOT_FOUND.value(), exception.getMessage(), Instant.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    public ResponseEntity<StandardError> internalServerError(InternalServerErrorException exception, HttpServletRequest request) {
        log.error(exception.getMessage(), exception);
        String message = String.format("An internal error has occured. Details: %s", exception.getMessage());
        StandardError error = new StandardError(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, Instant.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<StandardError> ioException(IOException exception, HttpServletRequest request) {
        String message = String.format("Error while processing images. Details: ", exception.getMessage());
        log.error(message, exception);
        StandardError error = new StandardError(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, Instant.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(InvalidImagePreDefinedTypeException.class)
    public ResponseEntity<StandardError> invalidImagePreDefinedType(InvalidImagePreDefinedTypeException exception, HttpServletRequest request) {
        log.info(exception.getMessage());
        StandardError error = new StandardError(HttpStatus.BAD_REQUEST.value(), exception.getMessage(), Instant.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
