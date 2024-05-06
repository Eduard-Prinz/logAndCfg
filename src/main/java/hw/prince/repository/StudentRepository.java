package hw.prince.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import hw.prince.model.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByAge(int age);

    List<Student> findByAgeBetween(int min, int max);

    List<Student> findByFaculty_Id(long id);

    @Query(value = "SELECT count(id) FROM students", nativeQuery = true)
    int getCountOfStudents();

    @Query(value = "SELECT avg(age) FROM students", nativeQuery = true)
    double getAverageAgeOfStudents();

    @Query(value = "SELECT * FROM students ORDER BY id DESC LIMIT :count", nativeQuery = true)
    List<Student> getLastNStudents(@Param("count") int count);
}
