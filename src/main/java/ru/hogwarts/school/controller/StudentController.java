package ru.hogwarts.school.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
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

    @GetMapping("/{id}/faculty") // GET http://localhost:8080/student/1/faculty
    public ResponseEntity<Faculty> getFacultyByStudentId(@PathVariable Long id) {
        Student student = studentService.findStudent(id);
        if (student == null || student.getFaculty() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student.getFaculty());
    }

    @GetMapping("/age-between") // GET http://localhost:8080/student/age-between?max=10&min=15
    public ResponseEntity<Collection<Student>> getStudentForAge(@RequestParam Integer max,
                                                                @RequestParam Integer min) {
        Collection<Student> results = studentService.findByAgeBetween(min, max);
        if (results.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping // GET http://localhost:8080/student?age=11
    public ResponseEntity<Collection<Student>> getAllStudents(@RequestParam(required = false) Integer age,
                                                              @RequestParam(required = false) Integer course) {
        Collection<Student> result = studentService.findByAgeAndCourse(age, course);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping  //POST http://localhost:8080/student
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @PostMapping("/advance-course") //POST http://localhost:8080/student/advance-course
    public ResponseEntity advanceCourse() {
        studentService.advanceCourses();
        return ResponseEntity.ok().build();
    }

    @PutMapping //PUT http://localhost:8080/student
    public ResponseEntity<Student> editStudent(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.editStudent(student));
    }

    @DeleteMapping("/{id}") //DELETE http://localhost:8080/student/11
    public ResponseEntity deleteStudent(@PathVariable Long id) {
        studentService.delStudent(id);
        return ResponseEntity.ok().build();
    }
}
