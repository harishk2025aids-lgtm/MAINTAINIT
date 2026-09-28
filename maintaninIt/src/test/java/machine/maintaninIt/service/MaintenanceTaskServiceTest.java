package machine.maintaninIt.service;

import machine.maintaninIt.dto.MaintenanceTaskRequest;
import machine.maintaninIt.dto.MaintenanceTaskResponse;
import machine.maintaninIt.entity.*;
import machine.maintaninIt.exception.ActiveMaintenanceTaskException;
import machine.maintaninIt.exception.InvalidTechnicianException;
import machine.maintaninIt.exception.ResourceNotFoundException;
import machine.maintaninIt.repository.MachineRepository;
import machine.maintaninIt.repository.MaintenanceTaskRepository;
import machine.maintaninIt.repository.TechnicianRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceTaskServiceTest {

    @Mock
    private MaintenanceTaskRepository maintenanceTaskRepository;

    @Mock
    private MachineRepository machineRepository;

    @Mock
    private TechnicianRepository technicianRepository;

    @InjectMocks
    private MaintenanceTaskService maintenanceTaskService;

    @SuppressWarnings("null")
    @Test
    void createTask_shouldCreateTask() {
        Machine machine = new Machine();
        machine.setId(1L);
        machine.setName("Air Filter");
        machine.setMachineCode("AF-1");
        machine.setMaintenanceIntervalHours(300L);
        machine.setCurrentUsageHours(50L);
        machine.setLastMaintenanceUsageHours(0L);

        MaintenanceTaskRequest request = new MaintenanceTaskRequest(1L, null, "Filter service", "Replace filters", 300L, MaintenanceTaskStatus.OPEN, MaintenancePriority.HIGH, "Check filter airflow");

        when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));
        when(maintenanceTaskRepository.findByMachineAndStatusIn(eq(machine), anyList())).thenReturn(List.of());
        when(maintenanceTaskRepository.save(any(MaintenanceTask.class))).thenAnswer(invocation -> {
            MaintenanceTask task = invocation.getArgument(0);
            task.setId(2L);
            return task;
        });

        MaintenanceTaskResponse response = maintenanceTaskService.createTask(request);

        assertNotNull(response);
        assertEquals("Filter service", response.getTaskTitle());
    }

    @Test
    void createTask_shouldPreventDuplicateOpenTask() {
        Machine machine = new Machine();
        machine.setId(1L);
        machine.setName("Air Filter");
        machine.setMachineCode("AF-1");
        machine.setMaintenanceIntervalHours(300L);

        MaintenanceTaskRequest request = new MaintenanceTaskRequest(1L, null, "Filter service", "Replace filters", 300L, MaintenanceTaskStatus.OPEN, MaintenancePriority.HIGH, "Check filter airflow");

        when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));
        when(maintenanceTaskRepository.findByMachineAndStatusIn(eq(machine), anyList())).thenReturn(List.of(new MaintenanceTask()));

        assertThrows(ActiveMaintenanceTaskException.class, () -> maintenanceTaskService.createTask(request));
    }

    @Test
    void completeTask_shouldSetCompletedAndResetUsageCycle() {
        Machine machine = new Machine();
        machine.setId(1L);
        machine.setCurrentUsageHours(500L);
        machine.setMaintenanceIntervalHours(300L);
        machine.setLastMaintenanceUsageHours(0L);

        Technician technician = new Technician();
        technician.setId(7L);
        technician.setStatus(TechnicianStatus.BUSY);

        MaintenanceTask task = new MaintenanceTask();
        task.setId(9L);
        task.setMachine(machine);
        task.setTechnician(technician);
        task.setStatus(MaintenanceTaskStatus.IN_PROGRESS);
        task.setTaskTitle("Task");
        task.setScheduledUsageHours(500L);

        when(maintenanceTaskRepository.findById(9L)).thenReturn(Optional.of(task));

        MaintenanceTaskResponse response = maintenanceTaskService.completeTask(9L);

        assertEquals(MaintenanceTaskStatus.COMPLETED, response.getStatus());
        assertEquals(800L, response.getScheduledUsageHours());
        assertEquals(500L, machine.getLastMaintenanceUsageHours());
        assertEquals(TechnicianStatus.AVAILABLE, technician.getStatus());
    }

    @Test
    void assignTechnician_shouldRequireAvailableTechnician() {
        Machine machine = new Machine();
        machine.setId(1L);
        machine.setName("Air Filter");
        machine.setMachineCode("AF-1");

        Technician technician = new Technician();
        technician.setId(10L);
        technician.setStatus(TechnicianStatus.BUSY);

        MaintenanceTask task = new MaintenanceTask();
        task.setMachine(machine);
        task.setTaskTitle("Service");
        task.setScheduledUsageHours(400L);
        task.setStatus(MaintenanceTaskStatus.OPEN);

        when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));
        when(maintenanceTaskRepository.findByMachineAndStatusIn(eq(machine), anyList())).thenReturn(List.of());
        when(technicianRepository.findById(10L)).thenReturn(Optional.of(technician));

        assertThrows(InvalidTechnicianException.class, () -> maintenanceTaskService.createTask(new MaintenanceTaskRequest(1L, 10L, "Service", "desc", 400L, MaintenanceTaskStatus.OPEN, MaintenancePriority.MEDIUM, null)));
    }

    @Test
    void getTaskById_shouldThrowWhenTaskMissing() {
        when(maintenanceTaskRepository.findById(44L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> maintenanceTaskService.getTaskById(44L));
    }
}
