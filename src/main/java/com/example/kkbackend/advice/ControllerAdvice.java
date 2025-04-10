package com.example.kkbackend.advice;

import com.example.kkbackend.exception.ActiveRoundsCountException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {
    @ExceptionHandler({EntityNotFoundException.class})
    public ResponseEntity entityNotFoundExceptionHandler(EntityNotFoundException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler({ActiveRoundsCountException.class})
    public ResponseEntity activeRoundsCountExceptionHandler(ActiveRoundsCountException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }
}
