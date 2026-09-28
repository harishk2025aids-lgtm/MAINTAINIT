package machine.maintaninIt.service;

import machine.maintaninIt.dto.DashboardSummaryResponse;
import machine.maintaninIt.dto.MachineUsageResponse;
import machine.maintaninIt.dto.MaintenanceNotificationResponse;
import machine.maintaninIt.dto.MaintenanceStatusResponse;
import machine.maintaninIt.entity.*;
import machine.maintaninIt.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final MachineRepository machineRepository;
    private final MaintenanceTaskRepository maintenanceTaskRepository;
    private final TechnicianRepository technicianRepository;

    public DashboardService(MachineRepository machineRepository,
                           MaintenanceTaskRepository maintenanceTaskRepository,
                           TechnicianRepository technicianRepository) {
        this.machineRepository = machineRepository;
        this.maintenanceTaskRepository = maintenanceTaskRepository;
        this.technicianRepository = technicianRepository;
    }

    public DashboardSummaryResponse getDashboardSummary() {
        long totalMachines = machineRepository.count();
        long activeMachines = machineRepository.countByStatus(MachineStatus.ACTIVE);
        long pendingMaintenance = maintenanceTaskRepository.countByStatus(MaintenanceTaskStatus.OPEN)
                + maintenanceTaskRepository.countByStatus(MaintenanceTaskStatus.IN_PROGRESS);
        long overdueMaintenance = maintenanceTaskRepository.countByStatus(MaintenanceTaskStatus.OVERDUE);
        long completedMaintenance = maintenanceTaskRepository.countByStatus(MaintenanceTaskStatus.COMPLETED);
        long availableTechnicians = technicianRepository.countByStatus(TechnicianStatus.AVAILABLE);

        return DashboardSummaryResponse.builder()
                .totalMachines(totalMachines)
                .activeMachines(activeMachines)
                .pendingMaintenance(pendingMaintenance)
                .overdueMaintenance(overdueMaintenance)
                .completedMaintenance(completedMaintenance)
                .availableTechnicians(availableTechnicians)
                .build();
    }

    public List<MaintenanceStatusResponse> getMaintenanceStatus() {
        return machineRepository.findAll().stream()
                .map(machine -> {
                    long nextMaintenanceUsage = machine.getLastMaintenanceUsageHours() + machine.getMaintenanceIntervalHours();
                    boolean due = machine.getCurrentUsageHours() >= nextMaintenanceUsage;
                    return MaintenanceStatusResponse.builder()
                            .machineId(machine.getId())
                            .machineCode(machine.getMachineCode())
                            .name(machine.getName())
                            .machineStatus(machine.getStatus())
                            .currentUsageHours(machine.getCurrentUsageHours())
                            .maintenanceIntervalHours(machine.getMaintenanceIntervalHours())
                            .nextMaintenanceUsageHours(nextMaintenanceUsage)
                            .maintenanceDue(due)
                            .build();
                })
                .toList();
    }

    public List<MachineUsageResponse> getMachineUsage() {
        return machineRepository.findAll().stream()
                .map(machine -> MachineUsageResponse.builder()
                        .machineId(machine.getId())
                        .machineCode(machine.getMachineCode())
                        .name(machine.getName())
                        .currentUsageHours(machine.getCurrentUsageHours())
                        .lastMaintenanceUsageHours(machine.getLastMaintenanceUsageHours())
                        .maintenanceIntervalHours(machine.getMaintenanceIntervalHours())
                        .build())
                .toList();
    }

    public List<MaintenanceNotificationResponse> getNotifications() {
        List<MaintenanceNotificationResponse> notifications = new ArrayList<>();

        for (Machine machine : machineRepository.findAll()) {
            long nextMaintenanceUsage = machine.getLastMaintenanceUsageHours() + machine.getMaintenanceIntervalHours();
            long remainingHours = nextMaintenanceUsage - machine.getCurrentUsageHours();

            if (machine.getCurrentUsageHours() >= nextMaintenanceUsage) {
                notifications.add(MaintenanceNotificationResponse.builder()
                        .type("OVERDUE")
                        .machineName(machine.getName())
                        .machineCode(machine.getMachineCode())
                        .message("Maintenance is overdue for " + machine.getName() + ".")
                        .remainingHours(remainingHours)
                        .nextMaintenanceUsageHours(nextMaintenanceUsage)
                        .build());
                continue;
            }

            if (remainingHours <= 50) {
                notifications.add(MaintenanceNotificationResponse.builder()
                        .type("DUE_SOON")
                        .machineName(machine.getName())
                        .machineCode(machine.getMachineCode())
                        .message("Maintenance for " + machine.getName() + " is due soon.")
                        .remainingHours(remainingHours)
                        .nextMaintenanceUsageHours(nextMaintenanceUsage)
                        .build());
            }
        }

        return notifications;
    }
}
