package machine.maintaninIt.config;

import machine.maintaninIt.entity.*;
import machine.maintaninIt.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            MachineRepository machineRepository,
            TechnicianRepository technicianRepository,
            UsageLogRepository usageLogRepository,
            MaintenanceTaskRepository maintenanceTaskRepository) {
        return args -> {
            if (machineRepository.count() > 0) {
                return;
            }

            Machine machine1 = new Machine();
            machine1.setName("Hydraulic Press");
            machine1.setMachineCode("HP-100");
            machine1.setDescription("Primary hydraulic press in Assembly Line A");
            machine1.setMaintenanceIntervalHours(500L);
            machine1.setCurrentUsageHours(120L);
            machine1.setLastMaintenanceUsageHours(0L);
            machine1.setStatus(MachineStatus.ACTIVE);
            machine1.setCreatedAt(LocalDateTime.now());
            machine1.setUpdatedAt(LocalDateTime.now());
            machineRepository.save(machine1);

            Machine machine2 = new Machine();
            machine2.setName("CNC Lathe");
            machine2.setMachineCode("CN-250");
            machine2.setDescription("Precision machining center");
            machine2.setMaintenanceIntervalHours(600L);
            machine2.setCurrentUsageHours(450L);
            machine2.setLastMaintenanceUsageHours(150L);
            machine2.setStatus(MachineStatus.ACTIVE);
            machine2.setCreatedAt(LocalDateTime.now());
            machine2.setUpdatedAt(LocalDateTime.now());
            machineRepository.save(machine2);

            Machine machine3 = new Machine();
            machine3.setName("Packaging Conveyor");
            machine3.setMachineCode("PC-330");
            machine3.setDescription("Automated conveyor line packaging system");
            machine3.setMaintenanceIntervalHours(400L);
            machine3.setCurrentUsageHours(700L);
            machine3.setLastMaintenanceUsageHours(300L);
            machine3.setStatus(MachineStatus.ACTIVE);
            machine3.setCreatedAt(LocalDateTime.now());
            machine3.setUpdatedAt(LocalDateTime.now());
            machineRepository.save(machine3);

            Technician tech1 = new Technician();
            tech1.setName("Alice Johnson");
            tech1.setEmail("alice@maintainit.com");
            tech1.setPhone("+1-555-0101");
            tech1.setSpecialization("Hydraulics");
            tech1.setStatus(TechnicianStatus.AVAILABLE);
            technicianRepository.save(tech1);

            Technician tech2 = new Technician();
            tech2.setName("Marco Silva");
            tech2.setEmail("marco@maintainit.com");
            tech2.setPhone("+1-555-0102");
            tech2.setSpecialization("CNC");
            tech2.setStatus(TechnicianStatus.AVAILABLE);
            technicianRepository.save(tech2);

            Technician tech3 = new Technician();
            tech3.setName("Priya Nair");
            tech3.setEmail("priya@maintainit.com");
            tech3.setPhone("+1-555-0103");
            tech3.setSpecialization("Electrical Systems");
            tech3.setStatus(TechnicianStatus.AVAILABLE);
            technicianRepository.save(tech3);

            UsageLog log1 = new UsageLog();
            log1.setMachine(machine1);
            log1.setUsageHours(120L);
            log1.setLoggedAt(LocalDateTime.now().minusDays(8));
            log1.setRemarks("Routine operation");
            usageLogRepository.save(log1);

            UsageLog log2 = new UsageLog();
            log2.setMachine(machine2);
            log2.setUsageHours(180L);
            log2.setLoggedAt(LocalDateTime.now().minusDays(6));
            log2.setRemarks("Material processing");
            usageLogRepository.save(log2);

            UsageLog log3 = new UsageLog();
            log3.setMachine(machine3);
            log3.setUsageHours(220L);
            log3.setLoggedAt(LocalDateTime.now().minusDays(5));
            log3.setRemarks("Packaging line cycle");
            usageLogRepository.save(log3);

            UsageLog log4 = new UsageLog();
            log4.setMachine(machine1);
            log4.setUsageHours(90L);
            log4.setLoggedAt(LocalDateTime.now().minusDays(2));
            log4.setRemarks("Extra production run");
            usageLogRepository.save(log4);

            UsageLog log5 = new UsageLog();
            log5.setMachine(machine2);
            log5.setUsageHours(270L);
            log5.setLoggedAt(LocalDateTime.now().minusDays(1));
            log5.setRemarks("Precision tuning cycle");
            usageLogRepository.save(log5);

            MaintenanceTask task1 = new MaintenanceTask();
            task1.setMachine(machine1);
            task1.setTechnician(tech1);
            task1.setTaskTitle("Hydraulic fluid check");
            task1.setDescription("Inspect hydraulic oil and line pressure");
            task1.setScheduledUsageHours(500L);
            task1.setStatus(MaintenanceTaskStatus.OPEN);
            task1.setPriority(MaintenancePriority.HIGH);
            task1.setCreatedAt(LocalDateTime.now().minusDays(3));
            task1.setNotes("Ready for review");
            maintenanceTaskRepository.save(task1);

            MaintenanceTask task2 = new MaintenanceTask();
            task2.setMachine(machine2);
            task2.setTechnician(tech2);
            task2.setTaskTitle("CNC spindle calibration");
            task2.setDescription("Verify spindle alignment and tool wear");
            task2.setScheduledUsageHours(600L);
            task2.setStatus(MaintenanceTaskStatus.IN_PROGRESS);
            task2.setPriority(MaintenancePriority.CRITICAL);
            task2.setCreatedAt(LocalDateTime.now().minusDays(2));
            task2.setStartedAt(LocalDateTime.now().minusDays(1));
            task2.setNotes("Maintenance in progress");
            maintenanceTaskRepository.save(task2);

            MaintenanceTask task3 = new MaintenanceTask();
            task3.setMachine(machine3);
            task3.setTechnician(tech3);
            task3.setTaskTitle("Conveyor belt inspection");
            task3.setDescription("Check belt tension and sensors");
            task3.setScheduledUsageHours(400L);
            task3.setStatus(MaintenanceTaskStatus.COMPLETED);
            task3.setPriority(MaintenancePriority.MEDIUM);
            task3.setCreatedAt(LocalDateTime.now().minusDays(7));
            task3.setCompletedAt(LocalDateTime.now().minusDays(2));
            task3.setNotes("Completed successfully");
            maintenanceTaskRepository.save(task3);

            MaintenanceTask task4 = new MaintenanceTask();
            task4.setMachine(machine1);
            task4.setTechnician(tech1);
            task4.setTaskTitle("Safety guard servicing");
            task4.setDescription("Inspect emergency stops and guards");
            task4.setScheduledUsageHours(520L);
            task4.setStatus(MaintenanceTaskStatus.OVERDUE);
            task4.setPriority(MaintenancePriority.HIGH);
            task4.setCreatedAt(LocalDateTime.now().minusDays(10));
            task4.setNotes("Past due");
            maintenanceTaskRepository.save(task4);

            MaintenanceTask task5 = new MaintenanceTask();
            task5.setMachine(machine3);
            task5.setTechnician(tech3);
            task5.setTaskTitle("Sensor calibration");
            task5.setDescription("Verify packaging sensor calibration");
            task5.setScheduledUsageHours(430L);
            task5.setStatus(MaintenanceTaskStatus.OPEN);
            task5.setPriority(MaintenancePriority.LOW);
            task5.setCreatedAt(LocalDateTime.now().minusDays(1));
            task5.setNotes("Awaiting assignment");
            maintenanceTaskRepository.save(task5);
        };
    }
}
