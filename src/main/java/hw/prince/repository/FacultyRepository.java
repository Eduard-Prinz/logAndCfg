package hw.prince.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import hw.prince.model.Faculty;

import java.util.List;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    List<Faculty> findByColor(String color);

    List<Faculty> findByNameIgnoreCaseOrColorIgnoreCase(String name, String color);

}
