package ru.hogwarts.school.controller;

import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.FacultyService;
import ru.hogwarts.school.service.StudentService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacultyService facultyService;

    @MockitoBean
    private StudentService studentService;

    @Test
    public void getStudentNotFoundMockTest() throws Exception {
        long nonExistingId = 9999L;

        when(studentService.findStudent(nonExistingId)).thenReturn(null);

        mockMvc.perform(get("/student/" + nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    public void getStudentInfoTest() throws Exception {
        final long id = 1;
        final String name = "testName";
        final int age = 15;
        final int course = 5;
        Student student = new Student(id, name, age, course);

        when(studentService.findStudent(id)).thenReturn(student);

        mockMvc.perform(get("/student/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age))
                .andExpect(jsonPath("$.course").value(course));
    }

    @Test
    public void getFacultyByStudentIdTest() throws Exception {
        final long facultyId = 1;
        final String facultyName = "testFacultyName";
        final String color = "testColor";
        Faculty faculty = new Faculty(facultyId, facultyName, color);

        final long studentId = 1;
        final String studentName = "testStudentName";
        final int age = 15;
        final int course = 2;
        Student student = new Student(studentId, studentName, age, course);
        student.setFaculty(faculty);

        when(studentService.findStudent(studentId)).thenReturn(student);

        mockMvc.perform(get("/student/" + studentId + "/faculty")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(facultyName))
                .andExpect(jsonPath("$.id").value(facultyId))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    public void getStudentForAgeTest() throws Exception {
        final long id = 1;
        final String name = "testStudentName";
        final int age = 15;
        final int course = 2;
        Student student = new Student(id, name, age, course);
        List<Student> students = Collections.singletonList(student);

        when(studentService.findByAgeBetween(13, 16)).thenReturn(students);

        mockMvc.perform(get("/student/age-between?max=16&min=13")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].age").value(age))
                .andExpect(jsonPath("$[0].course").value(course));
    }

    @Test
    public void getAllStudentsTest() throws Exception {
        final long id = 1;
        final String name = "testStudentName";
        final int age = 15;
        final int course = 2;
        Student student = new Student(id, name, age, course);
        List<Student> students = Collections.singletonList(student);

        when(studentService.findByAgeAndCourse(any(), any())).thenReturn(students);

        mockMvc.perform(get("/student")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].age").value(age))
                .andExpect(jsonPath("$[0].course").value(course));
    }

    @Test
    public void createStudentTest() throws Exception {
        final long id = 1;
        final String name = "testStudentName";
        final int age = 15;
        final int course = 2;
        Student student = new Student(id, name, age, course);

        JSONObject studentObject = new JSONObject();
        studentObject.put("name", name);
        studentObject.put("age", age);
        studentObject.put("course", course);

        when(studentService.createStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(post("/student")
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age))
                .andExpect(jsonPath("$.course").value(course));
    }

    @Test
    public void advanceCourseTest() throws Exception {
        doNothing().when(studentService).advanceCourses();
        mockMvc.perform(post("/student/advance-course"))
                .andExpect(status().isOk());
        verify(studentService, times(1)).advanceCourses();
    }

    @Test
    public void editStudentTest() throws Exception {
        final long id = 1;
        final String name = "testStudentName";
        final int age = 15;
        final int course = 2;
        Student student = new Student(id, name, age, course);

        JSONObject studentObject = new JSONObject();
        studentObject.put("name", name);
        studentObject.put("age", age);
        studentObject.put("course", course);

        when(studentService.editStudent(any(Student.class))).thenReturn(student);

        mockMvc.perform(put("/student")
                        .content(studentObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.age").value(age))
                .andExpect(jsonPath("$.course").value(course));
    }

    @Test
    public void deleteStudentTest() throws Exception {
        final long id = 1;
        doNothing().when(studentService).delStudent(id);
        mockMvc.perform(delete("/student/" + id))
                .andExpect(status().isOk());
    }
}
