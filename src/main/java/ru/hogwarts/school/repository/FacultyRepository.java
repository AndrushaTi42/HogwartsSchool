package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.hogwarts.school.model.Faculty;

import java.util.Collection;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    //для игнорирования регистра с ру символами решил на таком варианте остановиться, раз уж SQL изучаем в уроке
    @Query(value = "SELECT * FROM faculty WHERE " +
            "(:name IS NULL OR LOWER(name COLLATE \"ru_RU\") LIKE LOWER(CONCAT('%', :name, '%') COLLATE \"ru_RU\")) AND " +
            "(:color IS NULL OR LOWER(color COLLATE \"ru_RU\") LIKE LOWER(CONCAT('%', :color, '%') COLLATE \"ru_RU\"))",
            nativeQuery = true)
    Collection<Faculty> findByNameAndColorFields(@Param("name") String name, @Param("color") String color);

    //но так же можно сделать вот так, но тогда кодировка ру символов не работает
//    Collection<Faculty> findByNameIgnoreCaseOrColorIgnoreCase(String name, String color);


}
