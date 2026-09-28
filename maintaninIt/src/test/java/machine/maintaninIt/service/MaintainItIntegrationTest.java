package machine.maintaninIt.service;

import machine.maintaninIt.dto.*;
import machine.maintaninIt.entity.*;
import machine.maintaninIt.exception.*;
import machine.maintaninIt.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unused")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MachineService.class, TechnicianService.class, UsageLogService.class, MaintenanceTaskService.class, DashboardService.class})
class MaintainItIntegrationTest {

    @Autowired
    private MachineRepository machineRepository;

    @Autowired
    private TechnicianRepository technicianRepository;

    @Autowired
    private UsageLogRepository usageLogRepository;

    @Autowired
    private MaintenanceTaskRepository maintenanceTaskRepository;

    @Autowired
    private MachineService machineService;

    @Autowired
    private TechnicianService technicianService;

    @Autowired
    private UsageLogService usageLogService;

    @Autowired
    private MaintenanceTaskService maintenanceTaskService;

    @Autowired
    private DashboardService dashboardService;

    @BeforeEach
    void cleanData() {
        maintenanceTaskRepository.deleteAll();
        usageLogRepository.deleteAll();
        machineRepository.deleteAll();
        technicianRepository.deleteAll();
    }

    @Test
    void shouldCreateMachineAndTechnician() {
        MachineResponse machine = machineService.createMachine(new MachineRequest("Boiler", "B-101", "Main boiler", 700L, 0L, 0L, MachineStatus.ACTIVE));
        TechnicianResponse technician = technicianService.createTechnician(new TechnicianRequest("Bob", "bob@mail.com", "11111111", "Mechanical", TechnicianStatus.AVAILABLE));

        assertNotNull(machine.getId());
        assertNotNull(technician.getId());
    }

    @SuppressWarnings("null")
    @Test
    void shouldLogUsageAndCreateTaskWhenDue() {
        Machine machine = new Machine();
        machine.setName("Mixer");
        machine.setMachineCode("MX-1");
        machine.setDescription("Mixing unit");
        machine.setMaintenanceIntervalHours(500L);
        machine.setCurrentUsageHours(0L);
        machine.setLastMaintenanceUsageHours(0L);
        machine.setStatus(MachineStatus.ACTIVE);
        machineRepository.save(machine);

        UsageLogResponse response = usageLogService.createUsageLog(new UsageLogRequest(machine.getId(), 500L, "Production run"));

        assertNotNull(response);
        assertEquals(500L, machineRepository.findById(machine.getId()).orElseThrow().getCurrentUsageHours());
        assertFalse(maintenanceTaskRepository.findByMachineId(machine.getId()).isEmpty());
    }

    @Test
    void shouldPreventDuplicateActiveTask() {
        Machine machine = new Machine();
        machine.setName("Compressor");
        machine.setMachineCode("CMP-1");
        machine.setDescription("Air compressor");
        machine.setMaintenanceIntervalHours(300L);
        machine.setCurrentUsageHours(100L);
        machine.setLastMaintenanceUsageHours(0L);
        machine.setStatus(MachineStatus.ACTIVE);
        machineRepository.save(machine);

        MaintenanceTask task = new MaintenanceTask();
        task.setMachine(machine);
        task.setTaskTitle("Inspect compressor");
        task.setScheduledUsageHours(300L);
        task.setStatus(MaintenanceTaskStatus.OPEN);
        task.setPriority(MaintenancePriority.MEDIUM);
        maintenanceTaskRepository.save(task);

        assertThrows(ActiveMaintenanceTaskException.class,
                () -> maintenanceTaskService.createTask(new MaintenanceTaskRequest(machine.getId(), null, "Duplicate task", "desc", 300L, MaintenanceTaskStatus.OPEN, MaintenancePriority.MEDIUM, "notes")));
    }

    @SuppressWarnings("null")
    @Test
    void shouldStartAndCompleteTask() {
        Machine machine = new Machine();
        machine.setName("Generator");
        machine.setMachineCode("GEN-1");
        machine.setDescription("Backup generator");
        machine.setMaintenanceIntervalHours(200L);
        machine.setCurrentUsageHours(150L);
        machine.setLastMaintenanceUsageHours(0L);
        machine.setStatus(MachineStatus.ACTIVE);
        machineRepository.save(machine);

        Technician technician = new Technician();
        technician.setName("Sam");
        technician.setEmail("sam@maintainit.com");
        technician.setPhone("555-0109");
        technician.setSpecialization("Electrical");
        technician.setStatus(TechnicianStatus.AVAILABLE);
        technicianRepository.save(technician);

        MaintenanceTask task = new MaintenanceTask();
        task.setMachine(machine);
        task.setTechnician(technician);
        task.setTaskTitle("Engine service");
        task.setScheduledUsageHours(200L);
        task.setStatus(MaintenanceTaskStatus.OPEN);
        task.setPriority(MaintenancePriority.HIGH);
        maintenanceTaskRepository.save(task);

        MaintenanceTaskResponse started = maintenanceTaskService.startTask(task.getId());
        assertEquals(MaintenanceTaskStatus.IN_PROGRESS, started.getStatus());

        MaintenanceTaskResponse completed = maintenanceTaskService.completeTask(task.getId());
        assertEquals(MaintenanceTaskStatus.COMPLETED, completed.getStatus());
        assertEquals(machine.getCurrentUsageHours(), machineRepository.findById(machine.getId()).orElseThrow().getLastMaintenanceUsageHours());
    }

    @Test
    void shouldMarkOverdueWhenUsageExceedsScheduledUsage() {
        Machine machine = new Machine();
        machine.setName("Boiler");
        machine.setMachineCode("B-11");
        machine.setDescription("Thermal boiler");
        machine.setMaintenanceIntervalHours(500L);
        machine.setCurrentUsageHours(700L);
        machine.setLastMaintenanceUsageHours(0L);
        machine.setStatus(MachineStatus.ACTIVE);
        machineRepository.save(machine);

        MaintenanceTask task = new MaintenanceTask();
        task.setMachine(machine);
        task.setTaskTitle("Boiler check");
        task.setScheduledUsageHours(500L);
        task.setStatus(MaintenanceTaskStatus.OPEN);
        task.setPriority(MaintenancePriority.HIGH);
        maintenanceTaskRepository.save(task);

        maintenanceTaskService.getOverdueTasks();

        @SuppressWarnings("null")
        MaintenanceTask updated = maintenanceTaskRepository.findById(task.getId()).orElseThrow();
        assertEquals(MaintenanceTaskStatus.OVERDUE, updated.getStatus());
    }

    @Test
    void shouldReturnDashboardStatistics() {
        Machine machine = new Machine();
        machine.setName("Drill");
        machine.setMachineCode("DR-1");
        machine.setDescription("Drill press");
        machine.setMaintenanceIntervalHours(250L);
        machine.setCurrentUsageHours(100L);
        machine.setLastMaintenanceUsageHours(0L);
        machine.setStatus(MachineStatus.ACTIVE);
        machineRepository.save(machine);

        Technician tech = new Technician();
        tech.setName("Jane");
        tech.setEmail("jane@maintainit.com");
        tech.setPhone("555-6321");
        tech.setSpecialization("Maintenance");
        tech.setStatus(TechnicianStatus.AVAILABLE);
        technicianRepository.save(tech);

        DashboardSummaryResponse summary = dashboardService.getDashboardSummary();
        assertEquals(1L, summary.getTotalMachines());
        assertEquals(1L, summary.getActiveMachines());
        assertEquals(1L, summary.getAvailableTechnicians());
    }
}
