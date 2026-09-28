package machine.maintaninIt.repository;

import machine.maintaninIt.entity.Machine;
import machine.maintaninIt.entity.MaintenancePriority;
import machine.maintaninIt.entity.MaintenanceTask;
import machine.maintaninIt.entity.MaintenanceTaskStatus;
import machine.maintaninIt.entity.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;

public interface MaintenanceTaskRepository extends JpaRepository<MaintenanceTask, Long> {
    List<MaintenanceTask> findByMachineId(Long machineId);
    List<MaintenanceTask> findByTechnicianId(Long technicianId);
    List<MaintenanceTask> findByStatus(MaintenanceTaskStatus status);
    List<MaintenanceTask> findByMachineAndStatusIn(Machine machine, Collection<MaintenanceTaskStatus> statuses);
    List<MaintenanceTask> findByTechnician(Technician technician);
    List<MaintenanceTask> findByPriority(MaintenancePriority priority);
    List<MaintenanceTask> findByMachineIdAndStatus(Long machineId, MaintenanceTaskStatus status);
    List<MaintenanceTask> findByTechnicianIdAndStatus(Long technicianId, MaintenanceTaskStatus status);
    long countByStatus(MaintenanceTaskStatus status);
}
