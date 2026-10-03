package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;


@Service
public class StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;

    private List<Long> extractIds(Collection<Student> students) {
        return students.stream().map(Student::getId).toList();
    }

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        logger.info("Was invoked method for create student");
        return studentRepository.save(student);
    }

    public Student findStudent(Long id) {
        logger.info("Was invoked method for find student");
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            logger.warn("Student with id = {} was not found", id);
            throw new StudentNotFoundException(id);
        } else {
            logger.debug("Found student: {}", student);
            return student;
        }
    }

    public Student editStudent(Student student) {
        logger.info("Was invoked method for edit student");
        if (!studentRepository.existsById(student.getId())) {
            logger.warn("Student with id = {} was not found for editing", student.getId());
            throw new StudentNotFoundException(student.getId());
        }
        Student updated = studentRepository.save(student);
        logger.debug("Student with id = {} was updated", updated.getId());
        return updated;
    }

    public void delStudent(Long id) {
        logger.info("Was invoked method for delete student");
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            logger.warn("Student with id = {} was not found", id);
            return;
        }
        studentRepository.deleteById(id);
        logger.debug("Student with id = {} was deleted", id);
    }

    //фильтр по возрасту и курсу
    public Collection<Student> findByAgeAndCourse(Integer age, Integer course) {
        logger.info("Was invoked method for find students by age and course");
        Collection<Student> students = studentRepository.findByAgeAndCourse(age, course);
        if (students.isEmpty()) {
            logger.warn("Student with age = {}, and course = {} was not found", age, course);
        } else {
            logger.debug("Found students with ids: {}", extractIds(students));
        }
        return students;
    }

    //фильтр по возрасту (от, до)
    public Collection<Student> findByAgeBetween(Integer min, Integer max) {
        logger.info("Was invoked method for find students by age range");
        Collection<Student> students = studentRepository.findByAgeBetween(min, max);
        if (students.isEmpty()) {
            logger.warn("Students with age between {} and {} were not found", min, max);
        } else {
            logger.debug("Found students: {}", extractIds(students));
        }
        return students;
    }

    //перевод на следующий курс и удаление выпускников
    public void advanceCourses() {
        logger.info("Was invoked method for advance courses");
        for (Student student : studentRepository.findAll()) {
            int newCourse = student.getCourse() + 1;
            if (newCourse > 7) {
                logger.warn("Student {} reached course limit and will be deleted", student.getId());
                studentRepository.delete(student);
            } else {
                logger.debug("Student {} advanced to course {}", student.getId(), newCourse);
                student.setCourse(newCourse);
                studentRepository.save(student);
            }
        }
    }

    public Long getCountStudents() {
        logger.info("Was invoked method for get count of students");
        Long count = studentRepository.getCountStudents();
        logger.debug("Total students count fetched: {}", count);
        return count;
    }

    public BigDecimal getAverageAgeStudents() {
        logger.info("Was invoked method for get average age students");
        BigDecimal averageAge = studentRepository.getAverageAgeStudents();
        logger.debug("Average age students is: {}", averageAge);
        return averageAge;
    }

    public Collection<Student> getLastStudents() {
        logger.info("Was invoked method for get last students");
        Collection<Student> students = studentRepository.getLastStudents();
        if (students.isEmpty()) {
            logger.warn("Students were not found");
        } else {
            logger.debug("Found last students: {}", extractIds(students));
        }
        return students;
    }
}
