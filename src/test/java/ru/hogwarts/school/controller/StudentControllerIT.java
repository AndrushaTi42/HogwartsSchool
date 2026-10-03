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
public class StudentControllerIT {
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
    public void getStudentNotFoundIT() throws Exception {
        org.springframework.http.ResponseEntity<Student> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/student/9999",
                Student.class
        );
        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.NOT_FOUND);
    }

    @Test
    public void getStudentInfoTest() throws Exception {
        Student student = new Student("testName", 15, 4);
        Student savedStudent = restTemplate.postForObject(
                "http://localhost:" + port + "/student",
                student,
                Student.class
        );
        this.studentId = savedStudent.getId();

        ResponseEntity<Student> responseEntity = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/student/" + savedStudent.getId(),
                Student.class
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        Student responseBody = responseEntity.getBody();

        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getName()).isEqualTo("testName");
        assertThat(responseBody.getAge()).isEqualTo(15);
        assertThat(responseBody.getCourse()).isEqualTo(4);

    }

    @Test
    public void getFacultyByStudentIdTest() throws Exception {
        Faculty faculty = new Faculty("testNameFaculty", "testColor");
        Faculty savedFaculty = restTemplate.postForObject(
                "http://localhost:" + port + "/faculty",
                faculty,
                Faculty.class
        );
        this.facultyId = savedFaculty.getId();

        Student student = new Student("testNameStudent", 15, 4);
        student.setFaculty(savedFaculty);
        Student savedStudent = restTemplate.postForObject(
                "http://localhost:" + port + "/student",
                student,
                Student.class
        );
        this.studentId = savedStudent.getId();

        ResponseEntity<String> response = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/student/" + savedStudent.getId() + "/faculty",
                String.class
        );

        String responseBody = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseBody).contains("testNameFaculty", "testColor");
    }

    @Test
    public void getStudentForAgeTest() throws Exception {
        Student student = new Student("testName", 27, 4);
        Student savedStudent = restTemplate.postForObject(
                "http://localhost:" + port + "/student",
                student,
                Student.class
        );
        this.studentId = savedStudent.getId();

        ResponseEntity<Student[]> response = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/student/age-between?max=28&min=25",
                Student[].class
        );
        Student[] responseBody = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody[0].getAge()).isEqualTo(27);
        assertThat(responseBody[0].getCourse()).isEqualTo(4);
    }

    @Test
    public void getAllStudentsTest() throws Exception {
        Student student = new Student("testName", 15, 4);
        Student savedStudent = restTemplate.postForObject(
                "http://localhost:" + port + "/student",
                student,
                Student.class
        );
        this.studentId = savedStudent.getId();

        ResponseEntity<String> response = this.restTemplate.getForEntity(
                "http://localhost:" + port + "/student?age=15&course=4",
                String.class
        );
        String responseBody = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseBody).contains("testName", "15", "4");
    }

    @Test
    public void createStudentTest() throws Exception {
        Student student = new Student("testName", 15, 4);
        ResponseEntity<Student> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/student",
                student,
                Student.class
        );
        Student saveStudent = response.getBody();
        this.studentId = saveStudent.getId();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(saveStudent.toString()).contains("testName", "15", "4");
    }

    @Test
    public void advanceCourseTest() throws Exception {
        Student student = new Student("testName", 15, 6);
        Student savedStudent = restTemplate.postForObject(
                "http://localhost:" + port + "/student",
                student,
                Student.class
        );
        this.studentId = savedStudent.getId();

        ResponseEntity<Void> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/student/advance-course",
                null,
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(org.springframework.http.HttpStatus.OK);

        Student updatedStudent = this.restTemplate.getForObject(
                "http://localhost:" + port + "/student/" + this.studentId,
                Student.class
        );
        assertThat(updatedStudent.getCourse()).isEqualTo(7);
    }

    @Test
    public void graduationDeleteTest() throws Exception {
        Student graduate = new Student("testName", 23, 7);
        Student savedGraduate = restTemplate.postForObject(
                "http://localhost:" + port + "/student",
                graduate,
                Student.class
        );
        ResponseEntity<Void> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/student/advance-course",
                null,
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Student checkDeleted = restTemplate.getForObject(
                "http://localhost:" + port + "/student/" + savedGraduate.getId(),
                Student.class
        );
        assertThat(checkDeleted).isNull();
    }


    @Test
    public void editStudentTest() throws Exception {
        Student student = new Student("testName", 15, 6);
        Student savedStudent = restTemplate.postForObject(
                "http://localhost:" + port + "/student",
                student,
                Student.class
        );
        this.studentId = savedStudent.getId();

        Student newStudent = new Student(this.studentId, "newTestName", 14, 5);
        HttpEntity<Student> studentRespose = new HttpEntity<>(newStudent);
        ResponseEntity<Void> responseEditStudent = restTemplate.exchange(
                "http://localhost:" + port + "/student",
                HttpMethod.PUT,
                studentRespose,
                Void.class
        );
        assertThat(responseEditStudent.getStatusCode()).isEqualTo(HttpStatus.OK);
        ResponseEntity<Student> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/student/" + this.studentId,
                Student.class
        );
        Student checkStudent = response.getBody();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(checkStudent.getId()).isEqualTo(studentId);
        assertThat(checkStudent.getCourse()).isEqualTo(5);
        assertThat(checkStudent.getAge()).isEqualTo(14);
        assertThat(checkStudent.getName()).isEqualTo("newTestName");
    }

    @Test
    public void deleteStudentTest() throws Exception {
        Student student = new Student("testName", 15, 6);
        Student savedStudent = restTemplate.postForObject(
                "http://localhost:" + port + "/student",
                student,
                Student.class
        );
        this.studentId = savedStudent.getId();

        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/student/" + studentId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Student delStudent = restTemplate.getForObject(
                "http://localhost:" + port + "/student/" + studentId,
                Student.class
        );
        assertThat(delStudent).isNull();
    }
}
