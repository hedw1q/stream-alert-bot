package com.hedw1q.honey_alerts.streamservice.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.NotSupportedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class AppExceptionHandler {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String entityNotFoundException(PersistenceException e) {
        return e.getMessage();
    }

    @ExceptionHandler(value = {EntityExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public String entityExistsException(PersistenceException e) {
        return e.getMessage();
    }
    @ExceptionHandler(value = {NotSupportedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String notSupportedException(NotSupportedException e) {
        return e.getMessage();
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Not valid request data: {}",e.getMessage());
        return e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    }
}
