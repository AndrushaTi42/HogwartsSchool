package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class StudentService {
    private long lastId;
    private Map<Long, Student> students = new HashMap<>();

    public Student createStudent(Student student) {
        student.setId(++lastId);
        students.put(lastId, student);
        return student;
    }

    public Student findStudent(Long id) {
        return students.get(id);
    }

    public Student editStudent(Student student) {
        if (students.containsKey(student.getId())) {
            students.put(student.getId(), student);
            return student;
        }
        return null;
    }

    public Student delStudent(Long id) {
        return students.remove(id);
    }

    public Collection<Student> getAll() {
        return students.values();
    }

    //фильтр по возрасту и курсу
    public Collection<Student> findByAgeAndCourse(int age, int course) {
        return students.values().stream()
                .filter(student -> student.getAge() == age)
                .filter(student -> student.getCourse() == course)
                .toList(); // Соберет отфильтрованных студентов в список
    }

    //перевод на следующий курс и удаление выпускников
    public Collection<Student> advanceCourses() {
        //увеличиваем курс
        for (Student student : students.values()) {
            int newCourse = student.getCourse();
            student.setCourse(++newCourse);
        }

        students.values().removeIf(student -> student.getCourse() > 7);
        return students.values();
    }
}
