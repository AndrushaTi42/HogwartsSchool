package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.math.BigDecimal;
import java.util.Collection;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s WHERE " +
            "(:age IS NULL OR s.age = :age) AND " +
            "(:course IS NULL OR s.course = :course)")
    Collection<Student> findByAgeAndCourse(Integer age, Integer course);

    Collection<Student> findByAgeBetween(Integer min, Integer max);

    @Query(value = "SELECT COUNT(*) FROM student", nativeQuery = true)
    Long getCountStudents();

    @Query(value = "SELECT AVG(age) FROM student", nativeQuery = true)
    BigDecimal getAverageAgeStudents();

    @Query(value = "SELECT * FROM student ORDER BY id DESC LIMIT 5", nativeQuery = true)
    Collection<Student> getLastStudents();
}
