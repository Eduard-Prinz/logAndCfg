package hw.prince.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import hw.prince.model.Faculty;
import hw.prince.exception.FacultyNotFoundException;
import hw.prince.exception.StudentNotFoundException;
import hw.prince.model.Student;
import hw.prince.repository.FacultyRepository;
import hw.prince.repository.StudentRepository;


import java.util.List;


@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(StudentRepository studentRepository,
                          FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }


    public Student create(Student student) {
        logger.info("method create was invoked");
        student.setId(null);
        fillFaculty(student.getFaculty(), student);
        return studentRepository.save(student);
    }

    public Student update(long id, Student student) {
        logger.warn("method update was invoked with parameters id ={}, studentName = {}", id, student.getName());
        return studentRepository.findById(id)
                .map(oldStudent -> {
                    oldStudent.setName(student.getName());
                    oldStudent.setAge(student.getAge());
                    fillFaculty(student.getFaculty(), oldStudent);
                    return studentRepository.save(oldStudent);
                })
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    public Student delete(long id) {
        logger.warn("method delete was invoked with parameter id ={},", id);
        return studentRepository.findById(id)
                .map(student -> {
                    studentRepository.delete(student);
                    return student;
                })
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    public Student get(long id) {
        logger.debug("method get was invoked with parameters id ={}", id);
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    public List<Student> findByAge(int age) {
        logger.info("method findByAge was invoked with parameter age ={}", age);
        return studentRepository.findByAge(age);
    }

    public List<Student> findByAgeBetween(int minAge, int maxAge) {
        logger.debug("method findByAgeBetween was invoked with parameters minAge ={}, maxAge = {}", minAge, maxAge);
        return studentRepository.findByAgeBetween(minAge, maxAge);
    }

    public Faculty findFaculty(long id) {
        logger.debug("method findFaculty was invoked with parameters id ={}", id);
        return get(id).getFaculty();
    }

    private void fillFaculty(Faculty faculty, Student student) {
        logger.debug("method findByAgeBetween was invoked with parameters faculty ={}, student = {}", faculty, student);
        if (faculty != null && faculty.getId() != null) {
            Faculty facultyFromDb = facultyRepository.findById(faculty.getId())
                    .orElseThrow(() -> new FacultyNotFoundException(faculty.getId()));
            student.setFaculty(facultyFromDb);
        }
    }

    public int getCountOfStudents() {
        logger.debug("method getCountOfStudents was invoked");
        return studentRepository.getCountOfStudents();
    }

    public double getAverageAgeOfStudents() {
        logger.debug("method getAverageAgeOfStudents was invoked");
        return studentRepository.getAverageAgeOfStudents();
    }

    public List<Student> getLastNStudents(int count) {
        logger.debug("method getLastNStudents was invoked with parameters count ={}", count);
        return studentRepository.getLastNStudents(count);
    }
}
