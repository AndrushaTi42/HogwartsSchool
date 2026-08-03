package ru.hogwarts.school.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.*;

@RestController
@RequestMapping("/student")
public class StudentController {

    private StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{id}") //GET http://localhost:8080/student/11
    public ResponseEntity<Student> getStudentInfo(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @GetMapping // GET http://localhost:8080/student?age=11
    public ResponseEntity<Collection<Student>> getAllStudents(@RequestParam(required = false) Integer age,
                                                              @RequestParam(required = false) Integer course) {
        Collection<Student> result = studentService.findByAgeAndCourse(age, course);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(studentService.getAll());
    }

    @PostMapping  //POST http://localhost:8080/student
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @PostMapping("/advance-course")
    public ResponseEntity<Collection<Student>> advanceCourse() {
        Collection<Student> result = studentService.advanceCourses();
        return ResponseEntity.ok(result);
    }

    @PutMapping //PUT http://localhost:8080/student
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        Student foundStudent = studentService.editStudent(student);
        if (foundStudent == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundStudent);
    }

    @DeleteMapping("/{id}") //DELETE http://localhost:8080/student/11
    public Student deleteStudent(@PathVariable Long id) {
        return studentService.delStudent(id);
    }
}
