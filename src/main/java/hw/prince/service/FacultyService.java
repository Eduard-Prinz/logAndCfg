package hw.prince.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import hw.prince.model.Student;
import hw.prince.exception.FacultyNotFoundException;
import hw.prince.model.Faculty;
import hw.prince.repository.FacultyRepository;
import hw.prince.repository.StudentRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    public FacultyService(FacultyRepository facultyRepository,
                          StudentRepository studentRepository) {
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
    }

    public Faculty create(Faculty faculty) {
        logger.info("method create was invoked with parameter faculty ={},", faculty);
        faculty.setId(null);
        return facultyRepository.save(faculty);
    }

    public Faculty update(long id, Faculty faculty) {
        logger.warn("method update was invoked with parameters id ={}, faculty = {}", id, faculty.getName());
        return facultyRepository.findById(id)
                .map(oldFaculty -> {
                    oldFaculty.setName(faculty.getName());
                    oldFaculty.setColor(faculty.getColor());
                    return facultyRepository.save(oldFaculty);
                })
                .orElseThrow(() -> new FacultyNotFoundException(id));
    }

    public Faculty delete(long id) {
        logger.warn("method delete was invoked with parameter id ={},", id);
        return facultyRepository.findById(id)
                .map(faculty -> {
                    facultyRepository.delete(faculty);
                    return faculty;
                })
                .orElseThrow(() -> new FacultyNotFoundException(id));
    }

    public Faculty get(long id) {
        logger.debug("method get was invoked with parameter id ={},", id);
        return facultyRepository.findById(id)
                .orElseThrow(() -> new FacultyNotFoundException(id));
    }

    public List<Faculty> findByColor(String color) {
        logger.debug("method findByColor was invoked with parameter color ={},", color);
        return facultyRepository.findByColor(color);
    }

    public List<Faculty> findNameOrColor(String nameOrColor) {
        logger.debug("method findNameOrColor was invoked with parameter nameOrColor ={},", nameOrColor);
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(nameOrColor, nameOrColor);
    }

    public List<Student> findStudents(long id) {
        logger.debug("method findStudents was invoked with parameter id ={},", id);
        Faculty faculty = get(id);
        return studentRepository.findByFaculty_Id(faculty.getId());
    }
}
