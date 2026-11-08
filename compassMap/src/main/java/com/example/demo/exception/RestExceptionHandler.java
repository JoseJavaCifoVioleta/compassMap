package com.example.demo.exception;

import com.example.demo.controller.AssessmentViewRestController;
import com.example.demo.controller.CustomerProfileRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

// Minimal mapping of the service runtime errors to 4xx responses.
// Limited to the controllers that validate referenced entities.
@RestControllerAdvice(assignableTypes = {AssessmentViewRestController.class, CustomerProfileRestController.class})
public class RestExceptionHandler {

    // referenced entity does not exist -> 404
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", exception.getMessage()));
    }

    // invalid request (for example, a Q&A without a question id) -> 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", exception.getMessage()));
    }
}
