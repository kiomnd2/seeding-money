package com.kakaopay.seedingmoeny.controller;

import com.kakaopay.seedingmoeny.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidAccessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<SeedingResponse<String>> validate(InvalidAccessException e) {
        return ResponseEntity.badRequest().body(SeedingResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<SeedingResponse<String>> validate(InvalidTokenException e) {
        return ResponseEntity.badRequest().body(SeedingResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(DuplicateCropsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<SeedingResponse<String>> validate(DuplicateCropsException e) {
        return ResponseEntity.badRequest().body(SeedingResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(NothingMoneyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<SeedingResponse<String>> validate(NothingMoneyException e) {
        return ResponseEntity.badRequest().body(SeedingResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(SelfCropsMoneyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<SeedingResponse<String>> validate(SelfCropsMoneyException e) {
        return ResponseEntity.badRequest().body(SeedingResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(ExpiredSearchDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<SeedingResponse<String>> validate(ExpiredSearchDateException e) {
        return ResponseEntity.badRequest().body(SeedingResponse.fail(e.getMessage()));
    }

    @ExceptionHandler({RequestFailedException.class, ObjectOptimisticLockingFailureException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<SeedingResponse<String>> validate(RequestFailedException e) {
        return ResponseEntity.badRequest().body(SeedingResponse.fail(e.getMessage()));
    }

}
