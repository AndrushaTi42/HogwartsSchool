package ru.hogwarts.school.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;

import java.util.Collection;

@RestController
@RequestMapping("/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping("/{id}") //GET http://localhost:8080/faculty/11
    public ResponseEntity<Faculty> getFacultyInfo(@PathVariable Long id) {
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @GetMapping //GET http://localhost:8080/faculty?name=a&color=red
    public ResponseEntity<Collection<Faculty>> getFacultyForNameOrColor(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color) {
        Collection<Faculty> results = facultyService.findByNameAndColorFields(name, color);
        if (results.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}/students") // GET http://localhost:8080/faculty/2/students
    public ResponseEntity<Collection<Student>> getStudentsByFacultyId(@PathVariable Long id) {
        Faculty faculty = facultyService.findFaculty(id);
        if (faculty == null || faculty.getStudents() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty.getStudents());
    }

    @PostMapping  //POST http://localhost:8080/faculty
    public Faculty createFaculty(@RequestBody Faculty faculty) {
        return facultyService.createFaculty(faculty);
    }

    @PutMapping //PUT http://localhost:8080/faculty
    public ResponseEntity<Faculty> editFaculty(@RequestBody Faculty faculty) {
        Faculty foundFaculty = facultyService.editFaculty(faculty);
        if (foundFaculty == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(foundFaculty);
    }

    @DeleteMapping("/{id}") //DELETE http://localhost:8080/faculty/11
    public ResponseEntity deleteFaculty(@PathVariable Long id) {
        facultyService.delFaculty(id);
        return ResponseEntity.ok().build();
    }
}
