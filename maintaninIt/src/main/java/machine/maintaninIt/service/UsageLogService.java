package machine.maintaninIt.service;

import machine.maintaninIt.dto.UsageLogRequest;
import machine.maintaninIt.dto.UsageLogResponse;
import machine.maintaninIt.entity.*;
import machine.maintaninIt.exception.InvalidMaintenanceTaskException;
import machine.maintaninIt.exception.ResourceNotFoundException;
import machine.maintaninIt.repository.MachineRepository;
import machine.maintaninIt.repository.MaintenanceTaskRepository;
import machine.maintaninIt.repository.UsageLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsageLogService {

    private final UsageLogRepository usageLogRepository;
    private final MachineRepository machineRepository;
    private final MaintenanceTaskRepository maintenanceTaskRepository;

    public UsageLogService(UsageLogRepository usageLogRepository,
                          MachineRepository machineRepository,
                          MaintenanceTaskRepository maintenanceTaskRepository) {
        this.usageLogRepository = usageLogRepository;
        this.machineRepository = machineRepository;
        this.maintenanceTaskRepository = maintenanceTaskRepository;
    }

    @Transactional
    public UsageLogResponse createUsageLog(UsageLogRequest request) {
        @SuppressWarnings("null")
        Machine machine = machineRepository.findById(request.getMachineId())
                .orElseThrow(() -> new ResourceNotFoundException("Machine with id " + request.getMachineId() + " was not found"));

        if (request.getUsageHours() == null || request.getUsageHours() < 0) {
            throw new InvalidMaintenanceTaskException("Usage hours cannot be negative");
        }

        UsageLog usageLog = new UsageLog();
        usageLog.setMachine(machine);
        usageLog.setUsageHours(request.getUsageHours());
        usageLog.setRemarks(request.getRemarks());

        UsageLog savedLog = usageLogRepository.save(usageLog);

        Long newUsage = machine.getCurrentUsageHours() + request.getUsageHours();
        machine.setCurrentUsageHours(newUsage);
        machine.setStatus(MachineStatus.ACTIVE);

        Long nextMaintenanceThreshold = machine.getLastMaintenanceUsageHours() + machine.getMaintenanceIntervalHours();
        if (newUsage >= nextMaintenanceThreshold) {
            List<MaintenanceTask> activeTasks = maintenanceTaskRepository.findByMachineAndStatusIn(
                    machine,
                    List.of(MaintenanceTaskStatus.OPEN, MaintenanceTaskStatus.IN_PROGRESS)
            );

            if (!activeTasks.isEmpty()) {
                throw new InvalidMaintenanceTaskException("An active maintenance task already exists for this machine.");
            }

            MaintenanceTask task = new MaintenanceTask();
            task.setMachine(machine);
            task.setTaskTitle("Preventive Maintenance - " + machine.getName());
            task.setDescription("Scheduled maintenance triggered after " + newUsage + " usage hours");
            task.setScheduledUsageHours(nextMaintenanceThreshold);
            task.setStatus(MaintenanceTaskStatus.OPEN);
            task.setPriority(newUsage >= nextMaintenanceThreshold + machine.getMaintenanceIntervalHours() / 2
                    ? MaintenancePriority.CRITICAL
                    : MaintenancePriority.MEDIUM);
            maintenanceTaskRepository.save(task);
        }

        machineRepository.save(machine);
        return mapToResponse(savedLog);
    }

    public List<UsageLogResponse> getAllUsageLogs() {
        return usageLogRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public UsageLogResponse getUsageLogById(Long id) {
        @SuppressWarnings("null")
        UsageLog usageLog = usageLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usage log with id " + id + " was not found"));
        return mapToResponse(usageLog);
    }

    @Transactional
    public UsageLogResponse updateUsageLog(Long id, UsageLogRequest request) {
        UsageLog usageLog = usageLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usage log with id " + id + " was not found"));
        if (request.getUsageHours() == null || request.getUsageHours() < 0) {
            throw new InvalidMaintenanceTaskException("Usage hours cannot be negative");
        }

        Machine oldMachine = usageLog.getMachine();
        Machine newMachine = machineRepository.findById(request.getMachineId())
                .orElseThrow(() -> new ResourceNotFoundException("Machine with id " + request.getMachineId() + " was not found"));
        oldMachine.setCurrentUsageHours(Math.max(0L, oldMachine.getCurrentUsageHours() - usageLog.getUsageHours()));
        newMachine.setCurrentUsageHours(newMachine.getCurrentUsageHours() + request.getUsageHours());
        usageLog.setMachine(newMachine);
        usageLog.setUsageHours(request.getUsageHours());
        usageLog.setRemarks(request.getRemarks());
        machineRepository.save(oldMachine);
        machineRepository.save(newMachine);
        return mapToResponse(usageLogRepository.save(usageLog));
    }

    @Transactional
    public void deleteUsageLog(Long id) {
        UsageLog usageLog = usageLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usage log with id " + id + " was not found"));
        Machine machine = usageLog.getMachine();
        machine.setCurrentUsageHours(Math.max(0L, machine.getCurrentUsageHours() - usageLog.getUsageHours()));
        machineRepository.save(machine);
        usageLogRepository.delete(usageLog);
    }

    @SuppressWarnings("null")
    public List<UsageLogResponse> getUsageLogsByMachine(Long machineId) {
        machineRepository.findById(machineId)
                .orElseThrow(() -> new ResourceNotFoundException("Machine with id " + machineId + " was not found"));

        return usageLogRepository.findByMachineId(machineId).stream().map(this::mapToResponse).toList();
    }

    public UsageLogResponse mapToResponse(UsageLog usageLog) {
        UsageLogResponse response = new UsageLogResponse();
        response.setId(usageLog.getId());
        response.setMachineId(usageLog.getMachine() != null ? usageLog.getMachine().getId() : null);
        response.setUsageHours(usageLog.getUsageHours());
        response.setLoggedAt(usageLog.getLoggedAt());
        response.setRemarks(usageLog.getRemarks());
        return response;
    }
}
