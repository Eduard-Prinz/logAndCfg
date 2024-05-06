package hwtests.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.datafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import hw.prince.model.Faculty;
import hw.prince.model.Student;
import hw.prince.repository.FacultyRepository;
import hw.prince.repository.StudentRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTestRestTemplateTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    private final Faker faker = new Faker();
    private final List<Student> students = new ArrayList<>(10);

    @Autowired
    private TestRestTemplate testRestTemplate;

    @AfterEach
    public void afterEach() {
        studentRepository.deleteAll();
        facultyRepository.deleteAll();
    }

    @BeforeEach
    public void beforeEach() {
        Faculty faculty1 = createFaculty();
        Faculty faculty2 = createFaculty();
        createStudents(faculty1, faculty2);
    }

    private Faculty createFaculty() {
        Faculty faculty = new Faculty();
        faculty.setName(faker.harryPotter().house());
        faculty.setColor(faker.color().name());
        return facultyRepository.save(faculty);
    }

    private void createStudents(Faculty... faculties) {
        students.clear();
        Stream.of(faculties)
                .forEach(faculty ->
                        students.addAll(
                                studentRepository.saveAll(Stream.generate(() -> {
                                            Student student = new Student();
                                            student.setFaculty(faculty);
                                            student.setName(faker.harryPotter().character());
                                            student.setAge(faker.random().nextInt(11, 18));
                                            return student;
                                        })
                                        .limit(5)
                                        .collect(Collectors.toList()))
                        )
                );
    }

    private String buildUrl(String uriStartsWithSlash) {
        return "http://localhost:%d%s".formatted(port, uriStartsWithSlash);
    }


    @Test
    public void createStudentWithoutFacultyPositive() {
        Student student = new Student();
        student.setAge(faker.random().nextInt(11, 18));
        student.setName(faker.harryPotter().character());

        createStudentPositive(student);
    }


    @Test
    public void createStudentWithFacultyPositive() {
        Student student = new Student();
        student.setAge(faker.random().nextInt(11, 18));
        student.setName(faker.harryPotter().character());

        Faculty randomFacultyFromDb = facultyRepository.findAll(PageRequest.of(faker.random().nextInt(0, 1), 1))
                .getContent()
                .get(0);

        student.setFaculty(randomFacultyFromDb);

        createStudentPositive(student);
    }

    @Test
    public void createStudentWithoutFacultyWhichNotExistsNegative() {
        Student student = new Student();
        student.setAge(faker.random().nextInt(11, 18));
        student.setName(faker.harryPotter().character());

        Faculty faculty = new Faculty();
        faculty.setId(-1L);

        student.setFaculty(faculty);

        ResponseEntity<String> responseEntity = testRestTemplate.postForEntity(
                buildUrl("/student"),
                student,
                String.class
        );
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isEqualTo("Faculty with id: %d not found!".formatted(-1));
    }


    @Test
    public void findByAgeBetweenTest() {
        int minAge = faker.random().nextInt(11, 18);
        int maxAge = faker.random().nextInt(minAge, 18);
        List<Student> expected = students.stream()
                .filter(student -> student.getAge() >= minAge && student.getAge() <= maxAge)
                .toList();

        ResponseEntity<List<Student>> responseEntity = testRestTemplate.exchange(
                buildUrl("/student?minAge={minAge}&maxAge={maxAge}"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Student>>() {
                },
                Map.of("minAge", minAge, "maxAge", maxAge)
        );
        List<Student> actual = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual).usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
    }

    @Test
    public void findFacultyPositiveTest() {
        Student student = students.get(faker.random().nextInt(students.size()));

        ResponseEntity<Faculty> responseEntity = testRestTemplate.getForEntity(
                buildUrl("/student/{id}/faculty"),
                Faculty.class,
                Map.of("id", student.getId())
        );
        Faculty actual = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(student.getFaculty());
    }

    @Test
    public void findFacultyNegativeTest() {
        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(
                buildUrl("/student/{id}/faculty"),
                String.class,
                Map.of("id", -1)
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseEntity.getBody()).isEqualTo("Student with id: %d not found!".formatted(-1));
    }

    public void createStudentPositive(Student student) {
        ResponseEntity<Student> responseEntity = testRestTemplate.postForEntity(
                buildUrl("/student"),
                student,
                Student.class
        );
        Student created = responseEntity.getBody();

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(created).isNotNull();
        assertThat(created).usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(student);
        assertThat(created.getId()).isNotNull();

        Optional<Student> fromDb = studentRepository.findById(created.getId());

        assertThat(fromDb).isPresent();
        assertThat(fromDb.get())
                .usingRecursiveComparison()
                .isEqualTo(created);
    }
}
