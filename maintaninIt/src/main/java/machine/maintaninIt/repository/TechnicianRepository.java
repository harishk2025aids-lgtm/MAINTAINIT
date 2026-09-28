package machine.maintaninIt.repository;

import machine.maintaninIt.entity.Technician;
import machine.maintaninIt.entity.TechnicianStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TechnicianRepository extends JpaRepository<Technician, Long> {
    Optional<Technician> findByEmail(String email);
    List<Technician> findByStatus(TechnicianStatus status);
    long countByStatus(TechnicianStatus status);
}
