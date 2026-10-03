package ru.hogwarts.school.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<Void> handleStudentNotFound(StudentNotFoundException ex) {
        logger.warn("Handled StudentNotFoundException: {}", ex.getMessage());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(FacultyNotFoundException.class)
    public ResponseEntity<Void> handleFacultyNotFound(FacultyNotFoundException ex) {
        logger.warn("Handled FacultyNotFoundException: {}", ex.getMessage());
        return ResponseEntity.notFound().build();
    }
}