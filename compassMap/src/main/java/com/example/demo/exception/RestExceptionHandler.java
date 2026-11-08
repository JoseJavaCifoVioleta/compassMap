package com.example.demo.exception;

import com.example.demo.controller.AssessmentViewRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

// Minimal mapping of the assessment service runtime errors to 4xx responses.
// Limited to AssessmentViewRestController so other endpoints keep their behavior.
@RestControllerAdvice(assignableTypes = AssessmentViewRestController.class)
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
