package ru.hogwarts.school.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.Student;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class FacultyControllerIT {
    @LocalServerPort
    private int port;

    private Long facultyId;
    private Long studentId;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private StudentController studentController;

    @Autowired
    private TestRestTemplate restTemplate;

    @AfterEach
    void tearDown() {
        if (studentId != null) {
            studentController.deleteStudent(studentId);
            studentId = null;
        }
        if (facultyId != null) {
            facultyController.deleteFaculty(facultyId);
            facultyId = null;
        }
    }

    @Test
    public void getFacultyNotFoundIT() throws Exception {
        org.springframework.http.ResponseEntity<Faculty> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/faculty/9999", Faculty.class
        );

        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
    }

    @Test
    public void getFacultyInfoTest() throws Exception {
        Faculty faculty = new Faculty("testName", "testColor");
        Faculty savedFaculty = restTemplate.postForObject(
                "http://localhost:" + port + "/faculty",
                faculty,
                Faculty.class
        );
        this.facultyId = savedFaculty.getId();

        ResponseEntity<Faculty> response = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/faculty/" + savedFaculty.getId(),
                Faculty.class
        );
        Faculty responseBody = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getName()).isEqualTo("testName");
    }

    @Test
    public void getFacultyForNameOrColorTest() throws Exception {
        Faculty faculty = new Faculty("testName", "testColor");
        Faculty savedFaculty = restTemplate.postForObject(
                "http://localhost:" + port + "/faculty",
                faculty,
                Faculty.class
        );
        this.facultyId = savedFaculty.getId();

        ResponseEntity<Faculty[]> response = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/faculty?name=testName&color=testColor",
                Faculty[].class
        );
        Faculty[] responseBody = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody[0].getColor()).isEqualTo("testColor");
        assertThat(responseBody[0].getName()).isEqualTo("testName");
    }

    @Test
    public void getStudentsByFacultyIdTest() throws Exception {
        Faculty faculty = new Faculty("testNameFaculty", "testColor");
        Faculty savedFaculty = restTemplate.postForObject(
                "http://localhost:" + port + "/faculty",
                faculty,
                Faculty.class
        );
        this.facultyId = savedFaculty.getId();
        Student student = new Student("testNameStudent", 15, 3);
        student.setFaculty(savedFaculty);
        Student savedStudent = restTemplate.postForObject(
                "http://localhost:" + port + "/student",
                student,
                Student.class
        );
        this.studentId = savedStudent.getId();
        ResponseEntity<String> response = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/faculty/" + facultyId + "/students",
                String.class
        );
        String responseBody = response.getBody();
        assertThat(responseBody).contains("testNameStudent", "15", "3", "testColor");
    }

    @Test
    public void createFacultyTest() throws Exception {
        Faculty faculty = new Faculty("testName", "testColor");
        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/faculty",
                faculty,
                Faculty.class
        );
        Faculty savedFaculty = response.getBody();
        this.facultyId = savedFaculty.getId();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(savedFaculty.toString()).contains("testName", "testColor");
    }

    @Test
    public void editFacultyTest() throws Exception {
        Faculty faculty = new Faculty("testName", "testColor");
        Faculty savedFaculty = restTemplate.postForObject(
                "http://localhost:" + port + "/faculty",
                faculty,
                Faculty.class
        );
        this.facultyId = savedFaculty.getId();

        Faculty newFaculty = new Faculty(facultyId, "testNameNew", "testColorNew");
        HttpEntity<Faculty> requestEntity = new HttpEntity<>(newFaculty);
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty",
                HttpMethod.PUT,
                requestEntity,
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Faculty> checkResponse = restTemplate.getForEntity(
                "http://localhost:" + port + "/faculty/" + this.facultyId,
                Faculty.class
        );
        Faculty checkFaculty = checkResponse.getBody();

        assertThat(checkResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(checkFaculty.getId()).isEqualTo(facultyId);
        assertThat(checkFaculty.getName()).isEqualTo("testNameNew");
        assertThat(checkFaculty.getColor()).isEqualTo("testColorNew");
    }

    @Test
    public void deleteFacultyTest() throws Exception {
        Faculty faculty = new Faculty("testName", "testColor");
        Faculty savedFaculty = restTemplate.postForObject(
                "http://localhost:" + port + "/faculty",
                faculty,
                Faculty.class
        );
        this.facultyId = savedFaculty.getId();

        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/faculty/" + facultyId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Faculty delFaculty = restTemplate.getForObject(
                "http://localhost:" + port + "/faculty/" + facultyId,
                Faculty.class
        );
        assertThat(delFaculty).isNull();
    }
}
