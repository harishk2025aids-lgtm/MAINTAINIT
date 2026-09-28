package machine.maintaninIt.repository;

import machine.maintaninIt.entity.Machine;
import machine.maintaninIt.entity.MachineStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MachineRepository extends JpaRepository<Machine, Long> {
    Optional<Machine> findByMachineCode(String machineCode);
    List<Machine> findByStatus(MachineStatus status);
    List<Machine> findByNameContainingIgnoreCase(String name);
    List<Machine> findByMachineCodeContainingIgnoreCase(String machineCode);
    long countByStatus(MachineStatus status);
}
