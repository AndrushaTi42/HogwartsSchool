package ru.hogwarts.school.exception;

public class FacultyNotFoundException extends RuntimeException {
    public FacultyNotFoundException(Long id) {
        super("Faculty with id = " + id + " was not found");
    }
}
