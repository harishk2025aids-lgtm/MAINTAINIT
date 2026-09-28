package machine.maintaninIt.service;

import machine.maintaninIt.dto.MaintenanceTaskRequest;
import machine.maintaninIt.dto.MaintenanceTaskResponse;
import machine.maintaninIt.entity.*;
import machine.maintaninIt.exception.*;
import machine.maintaninIt.repository.MachineRepository;
import machine.maintaninIt.repository.MaintenanceTaskRepository;
import machine.maintaninIt.repository.TechnicianRepository;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class MaintenanceTaskService {

    private final MaintenanceTaskRepository maintenanceTaskRepository;
    private final MachineRepository machineRepository;
    private final TechnicianRepository technicianRepository;

    public MaintenanceTaskService(MaintenanceTaskRepository maintenanceTaskRepository,
                                 MachineRepository machineRepository,
                                 TechnicianRepository technicianRepository) {
        this.maintenanceTaskRepository = maintenanceTaskRepository;
        this.machineRepository = machineRepository;
        this.technicianRepository = technicianRepository;
    }

    @SuppressWarnings("null")
    @Transactional
    public MaintenanceTaskResponse createTask(MaintenanceTaskRequest request) {
        Machine machine = machineRepository.findById(request.getMachineId())
                .orElseThrow(() -> new ResourceNotFoundException("Machine with id " + request.getMachineId() + " was not found"));

        List<MaintenanceTaskStatus> activeStatuses = List.of(MaintenanceTaskStatus.OPEN, MaintenanceTaskStatus.IN_PROGRESS);
        if (!maintenanceTaskRepository.findByMachineAndStatusIn(machine, activeStatuses).isEmpty()) {
            throw new ActiveMaintenanceTaskException("An active maintenance task already exists for this machine.");
        }

        MaintenanceTask task = new MaintenanceTask();
        task.setMachine(machine);
        task.setTaskTitle(request.getTaskTitle());
        task.setDescription(request.getDescription());
        task.setScheduledUsageHours(request.getScheduledUsageHours());
        task.setStatus(request.getStatus() != null ? request.getStatus() : MaintenanceTaskStatus.OPEN);
        task.setPriority(request.getPriority() != null ? request.getPriority() : MaintenancePriority.MEDIUM);
        task.setNotes(request.getNotes());

        if (request.getTechnicianId() != null) {
            assignTechnician(task, request.getTechnicianId());
        }

        machine.setStatus(MachineStatus.UNDER_MAINTENANCE);
        maintenanceTaskRepository.save(task);
        machineRepository.save(machine);
        return mapToResponse(task);
    }

    public List<MaintenanceTaskResponse> getAllTasks(String status, String priority, Long machineId, Long technicianId) {
        updateOverdueTasks();
        if (status != null && !status.isBlank()) {
            MaintenanceTaskStatus maintenanceStatus = MaintenanceTaskStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
            return maintenanceTaskRepository.findByStatus(maintenanceStatus).stream().map(this::mapToResponse).toList();
        }
        if (priority != null && !priority.isBlank()) {
            MaintenancePriority maintenancePriority = MaintenancePriority.valueOf(priority.trim().toUpperCase(Locale.ROOT));
            return maintenanceTaskRepository.findByPriority(maintenancePriority).stream().map(this::mapToResponse).toList();
        }
        if (machineId != null) {
            machineRepository.findById(machineId)
                    .orElseThrow(() -> new ResourceNotFoundException("Machine with id " + machineId + " was not found"));
            return maintenanceTaskRepository.findByMachineId(machineId).stream().map(this::mapToResponse).toList();
        }
        if (technicianId != null) {
            technicianRepository.findById(technicianId)
                    .orElseThrow(() -> new ResourceNotFoundException("Technician with id " + technicianId + " was not found"));
            return maintenanceTaskRepository.findByTechnicianId(technicianId).stream().map(this::mapToResponse).toList();
        }
        return maintenanceTaskRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public MaintenanceTaskResponse getTaskById(Long id) {
        MaintenanceTask task = findTaskById(id);
        updateOverdueTasks();
        return mapToResponse(task);
    }

    @Transactional
    public MaintenanceTaskResponse updateTask(Long id, MaintenanceTaskRequest request) {
        MaintenanceTask task = findTaskById(id);

        if (request.getTechnicianId() != null) {
            if (task.getTechnician() != null && task.getTechnician().getStatus() == TechnicianStatus.BUSY && !task.getTechnician().getId().equals(request.getTechnicianId())) {
                throw new InvalidTechnicianException("The current technician is already assigned and busy.");
            }
            assignTechnician(task, request.getTechnicianId());
        }

        task.setTaskTitle(request.getTaskTitle());
        task.setDescription(request.getDescription());
        task.setScheduledUsageHours(request.getScheduledUsageHours());
        task.setPriority(request.getPriority() != null ? request.getPriority() : task.getPriority());
        task.setNotes(request.getNotes());
        task.setStatus(request.getStatus() != null ? request.getStatus() : task.getStatus());

        if (task.getStatus() == MaintenanceTaskStatus.COMPLETED) {
            task.setCompletedAt(LocalDateTime.now());
            task.getMachine().setLastMaintenanceUsageHours(task.getMachine().getCurrentUsageHours());
            task.getMachine().setStatus(MachineStatus.ACTIVE);
            if (task.getTechnician() != null) {
                task.getTechnician().setStatus(TechnicianStatus.AVAILABLE);
            }
        }

        maintenanceTaskRepository.save(task);
        machineRepository.save(task.getMachine());
        return mapToResponse(task);
    }

    @SuppressWarnings("null")
    @Transactional
    public MaintenanceTaskResponse completeTask(Long id) {
        MaintenanceTask task = findTaskById(id);
        Machine machine = task.getMachine();

        if (task.getStatus() == MaintenanceTaskStatus.COMPLETED) {
            return mapToResponse(task);
        }

        task.setStatus(MaintenanceTaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        task.setScheduledUsageHours(machine.getCurrentUsageHours() + machine.getMaintenanceIntervalHours());
        machine.setLastMaintenanceUsageHours(machine.getCurrentUsageHours());
        machine.setStatus(MachineStatus.ACTIVE);

        if (task.getTechnician() != null) {
            task.getTechnician().setStatus(TechnicianStatus.AVAILABLE);
        }

        maintenanceTaskRepository.save(task);
        machineRepository.save(machine);
        return mapToResponse(task);
    }

    @SuppressWarnings("null")
    @Transactional
    public MaintenanceTaskResponse startTask(Long id) {
        MaintenanceTask task = findTaskById(id);
        if (task.getStatus() == MaintenanceTaskStatus.COMPLETED || task.getStatus() == MaintenanceTaskStatus.CANCELLED) {
            throw new InvalidTaskStatusException("Only open or overdue tasks can be started.");
        }

        task.setStatus(MaintenanceTaskStatus.IN_PROGRESS);
        task.setStartedAt(LocalDateTime.now());
        task.getMachine().setStatus(MachineStatus.UNDER_MAINTENANCE);
        if (task.getTechnician() != null) {
            if (task.getTechnician().getStatus() != TechnicianStatus.AVAILABLE) {
                throw new InvalidTechnicianException("Only AVAILABLE technicians can start maintenance tasks.");
            }
            task.getTechnician().setStatus(TechnicianStatus.BUSY);
        }

        maintenanceTaskRepository.save(task);
        machineRepository.save(task.getMachine());
        return mapToResponse(task);
    }

    public List<MaintenanceTaskResponse> getOpenTasks() {
        return maintenanceTaskRepository.findByStatus(MaintenanceTaskStatus.OPEN).stream().map(this::mapToResponse).toList();
    }

    public List<MaintenanceTaskResponse> getOverdueTasks() {
        updateOverdueTasks();
        return maintenanceTaskRepository.findByStatus(MaintenanceTaskStatus.OVERDUE).stream().map(this::mapToResponse).toList();
    }

    @SuppressWarnings("null")
    public List<MaintenanceTaskResponse> getTasksByMachine(Long machineId) {
        machineRepository.findById(machineId)
                .orElseThrow(() -> new ResourceNotFoundException("Machine with id " + machineId + " was not found"));
        return maintenanceTaskRepository.findByMachineId(machineId).stream().map(this::mapToResponse).toList();
    }

    @SuppressWarnings("null")
    public List<MaintenanceTaskResponse> getTasksByTechnician(Long technicianId) {
        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Technician with id " + technicianId + " was not found"));
        return maintenanceTaskRepository.findByTechnician(technician).stream().map(this::mapToResponse).toList();
    }

    @SuppressWarnings("null")
    public void deleteTask(Long id) {
        maintenanceTaskRepository.delete(findTaskById(id));
    }

    public MaintenanceTask findTaskById(@NonNull Long id) {
        return maintenanceTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance task with id " + id + " was not found"));
    }

    private void assignTechnician(MaintenanceTask task, @NonNull Long technicianId) {
        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Technician with id " + technicianId + " was not found"));

        if (technician.getStatus() != TechnicianStatus.AVAILABLE) {
            throw new InvalidTechnicianException("Only AVAILABLE technicians can be assigned to a task.");
        }

        task.setTechnician(technician);
        technician.setStatus(TechnicianStatus.BUSY);
    }

    private void updateOverdueTasks() {
        List<MaintenanceTask> tasks = maintenanceTaskRepository.findAll().stream()
                .filter(task -> task.getStatus() == MaintenanceTaskStatus.OPEN || task.getStatus() == MaintenanceTaskStatus.IN_PROGRESS)
                .toList();

        for (MaintenanceTask task : tasks) {
            Machine machine = task.getMachine();
            if (machine != null && machine.getCurrentUsageHours() > task.getScheduledUsageHours() && task.getStatus() != MaintenanceTaskStatus.COMPLETED) {
                task.setStatus(MaintenanceTaskStatus.OVERDUE);
                maintenanceTaskRepository.save(task);
            }
        }
    }

    public MaintenanceTaskResponse mapToResponse(MaintenanceTask task) {
        MaintenanceTaskResponse response = new MaintenanceTaskResponse();
        response.setId(task.getId());
        response.setMachineId(task.getMachine() != null ? task.getMachine().getId() : null);
        response.setTechnicianId(task.getTechnician() != null ? task.getTechnician().getId() : null);
        response.setTaskTitle(task.getTaskTitle());
        response.setDescription(task.getDescription());
        response.setScheduledUsageHours(task.getScheduledUsageHours());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setCreatedAt(task.getCreatedAt());
        response.setCompletedAt(task.getCompletedAt());
        response.setNotes(task.getNotes());
        return response;
    }
}
