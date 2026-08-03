package ru.hogwarts.school.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.Objects;

@Entity
public class Student {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Integer age;
    private Integer course;


    public Student() {
    }

    public Student(String name, Integer age, Integer course) {
        this.name = name;
        this.age = age;
        this.course = course;
    }

    public Student(Long id, String name, Integer age, Integer course) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.course = course;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCourse(Integer course) {
        this.course = course;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getCourse() {
        return course;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(age, student.age)
                && Objects.equals(id, student.id)
                && Objects.equals(name, student.name)
                && Objects.equals(course, student.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, course);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", course=" + course +
                '}';
    }
}
