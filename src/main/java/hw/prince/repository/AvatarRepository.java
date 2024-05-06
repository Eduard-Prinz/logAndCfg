package hw.prince.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import hw.prince.model.Avatar;

import java.util.Optional;

public interface AvatarRepository extends JpaRepository<Avatar, Long> {

    Optional<Avatar> findByStudent_Id(long student_Id);
}
