package com.example.demo.exception;

// Referenced entity does not exist.
// Extends IllegalArgumentException to keep the existing service contract.
public class NotFoundException extends IllegalArgumentException {

    public NotFoundException(String message) {
        super(message);
    }
}
