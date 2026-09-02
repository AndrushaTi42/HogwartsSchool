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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


@WebMvcTest(FacultyController.class)
public class FacultyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacultyService facultyService;

    @MockitoBean
    private StudentService studentService;

    @Test
    public void getFacultyNotFoundMockTest() throws Exception {
        long nonExistingId = 9999L;
        when(facultyService.findFaculty(nonExistingId)).thenReturn(null);

        mockMvc.perform(get("/faculty/" + nonExistingId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getFacultyInfoTest() throws Exception {
        final long id = 1;
        final String name = "testName";
        final String color = "testColor";
        Faculty faculty = new Faculty(id, name, color);

        when(facultyService.findFaculty(id)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    public void getFacultyForNameOrColorTest() throws Exception {
        final long id = 1;
        final String name = "testName";
        final String color = "testColor";
        Faculty faculty = new Faculty(id, name, color);
        List<Faculty> faculties = Collections.singletonList(faculty);


        when(facultyService.findByNameAndColorFields(name, color)).thenReturn(faculties);

        mockMvc.perform(get("/faculty")
                        .param("name", name)
                        .param("color", color)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(id))
                .andExpect(jsonPath("$[0].name").value(name))
                .andExpect(jsonPath("$[0].color").value(color));
    }

    @Test
    public void getStudentsByFacultyIdTest() throws Exception {
        final long facultyId = 1;
        final String facultyName = "testFacultyName";
        final String color = "testColor";
        Faculty faculty = new Faculty(facultyId, facultyName, color);

        final Long studentId = 1L;
        final String studentName = "testStudentName";
        final int age = 15;
        final int course = 2;
        Student student = new Student(studentId, studentName, age, course);
        student.setFaculty(faculty);
        List<Student> students = Collections.singletonList(student);
        faculty.setStudents(students);

        when(facultyService.findFaculty(facultyId)).thenReturn(faculty);

        mockMvc.perform(get("/faculty/" + facultyId + "/students")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(studentId))
                .andExpect(jsonPath("$[0].name").value(studentName))
                .andExpect(jsonPath("$[0].age").value(age))
                .andExpect(jsonPath("$[0].course").value(course));
    }

    @Test
    public void createFacultyTest() throws Exception {
        final long id = 1;
        final String name = "testFacultyName";
        final String color = "testColor";
        Faculty faculty = new Faculty(id, name, color);

        JSONObject facultyObject = new JSONObject();
        facultyObject.put("name", name);
        facultyObject.put("color", color);

        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(post("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    public void editFacultyTest() throws Exception {
        final long id = 1;
        final String name = "testFacultyName";
        final String color = "testColor";
        Faculty faculty = new Faculty(id, name, color);

        JSONObject facultyObject = new JSONObject();
        facultyObject.put("name", name);
        facultyObject.put("color", color);

        when(facultyService.editFaculty(any(Faculty.class))).thenReturn(faculty);

        mockMvc.perform(put("/faculty")
                        .content(facultyObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    public void deleteFacultyTest() throws Exception {
        final long id = 1;
        doNothing().when(facultyService).delFaculty(id);
        mockMvc.perform(delete("/faculty/" + id))
                .andExpect(status().isOk());
    }
}
