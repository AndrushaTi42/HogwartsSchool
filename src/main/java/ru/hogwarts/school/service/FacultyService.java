package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;


@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(Long id) {
        return facultyRepository.findById(id).orElse(null);
    }

    public Faculty editFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public void delFaculty(Long id) {
        facultyRepository.deleteById(id);
    }

    public Collection<Faculty> findByNameAndColorFields(String name, String color) {
        return facultyRepository.findByNameAndColorFields(name, color);
    }

//    public Collection<Faculty> findByNameAndColorFields(String name, String color) {
//        String searchName = (name == null || name.isBlank()) ? "" : name;
//        String searchColor = (color == null || color.isBlank()) ? "" : color;
//        return facultyRepository.findByNameAndColorFields(searchName, searchColor);
//    }
}
