package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;


@Service
public class StudentService {
    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    public Student findStudent(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    public Student editStudent(Student student) {
        return studentRepository.save(student);
    }

    public void delStudent(Long id) {
        studentRepository.deleteById(id);
    }

    //фильтр по возрасту и курсу
    public Collection<Student> findByAgeAndCourse(Integer age, Integer course) {
        return studentRepository.findByAgeAndCourse(age, course);
    }

    //фильтр по возрасту (от, до)
    public Collection<Student> findByAgeBetween(Integer min, Integer max) {
        return studentRepository.findByAgeBetween(min, max);
    }

    //перевод на следующий курс и удаление выпускников
    public void advanceCourses() {
        for (Student student : studentRepository.findAll()) {
            int newCourse = student.getCourse() + 1;
            if (newCourse > 7) {
                studentRepository.delete(student);
            } else {
                student.setCourse(newCourse);
                studentRepository.save(student);
            }
        }
    }
}
