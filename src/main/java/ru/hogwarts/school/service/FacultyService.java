package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.List;


@Service
public class FacultyService {
    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        return facultyRepository.save(faculty);
    }

    public Faculty findFaculty(Long id) {
        logger.info("Was invoked method for find faculty");
        Faculty faculty = facultyRepository.findById(id).orElse(null);
        if (faculty == null) {
            logger.warn("Faculty with id = {} was not found", id);
            throw new FacultyNotFoundException(id);
        } else {
            logger.debug("Found faculty: {}", faculty);
            return faculty;
        }
    }

    public Faculty editFaculty(Faculty faculty) {
        logger.info("Was invoked method for edit faculty");
        if (!facultyRepository.existsById(faculty.getId())) {
            logger.warn("Faculty with id = {} was not found for editing", faculty.getId());
            throw new FacultyNotFoundException(faculty.getId());
        }
        Faculty updated = facultyRepository.save(faculty);
        logger.debug("Faculty with id = {} was updated", updated.getId());
        return updated;
    }

    public void delFaculty(Long id) {
        logger.info("Was invoked method for delete faculty");
        Faculty faculty = facultyRepository.findById(id).orElse(null);
        if (faculty == null) {
            logger.warn("Faculty with id = {} was not found", id);
            return;
        }
        facultyRepository.deleteById(id);
        logger.debug("Faculty with id = {} was deleted", id);
    }

    public Collection<Faculty> findByNameAndColorFields(String name, String color) {
        logger.info("Was invoked method for find by name and color faculty");
        Collection<Faculty> faculties = facultyRepository.findByNameAndColorFields(name, color);
        if (faculties.isEmpty()) {
            logger.warn("Faculties with name = {}, and color = {} was not found", name, color);
        } else {
            List<Long> ids = faculties.stream()
                    .map(Faculty::getId)
                    .toList();
            logger.debug("Found faculties with ids: {}", ids);
        }
        return faculties;
    }

//    public Collection<Faculty> findByNameAndColorFields(String name, String color) {
//        String searchName = (name == null || name.isBlank()) ? "" : name;
//        String searchColor = (color == null || color.isBlank()) ? "" : color;
//        return facultyRepository.findByNameAndColorFields(searchName, searchColor);
//    }
}
