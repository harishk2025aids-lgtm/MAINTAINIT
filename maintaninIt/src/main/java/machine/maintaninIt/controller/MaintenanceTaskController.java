package machine.maintaninIt.controller;

import jakarta.validation.Valid;
import machine.maintaninIt.dto.MaintenanceTaskRequest;
import machine.maintaninIt.dto.MaintenanceTaskResponse;
import machine.maintaninIt.service.MaintenanceTaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-tasks")
public class MaintenanceTaskController {

    private final MaintenanceTaskService maintenanceTaskService;

    public MaintenanceTaskController(MaintenanceTaskService maintenanceTaskService) {
        this.maintenanceTaskService = maintenanceTaskService;
    }

    @PostMapping
    public ResponseEntity<MaintenanceTaskResponse> createTask(@Valid @RequestBody MaintenanceTaskRequest request) {
        return new ResponseEntity<>(maintenanceTaskService.createTask(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MaintenanceTaskResponse>> getAllTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long machine,
            @RequestParam(required = false) Long technician) {
        return ResponseEntity.ok(maintenanceTaskService.getAllTasks(status, priority, machine, technician));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceTaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceTaskService.getTaskById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceTaskResponse> updateTask(@PathVariable Long id, @Valid @RequestBody MaintenanceTaskRequest request) {
        return ResponseEntity.ok(maintenanceTaskService.updateTask(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        maintenanceTaskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<MaintenanceTaskResponse> completeTask(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceTaskService.completeTask(id));
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<MaintenanceTaskResponse> startTask(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceTaskService.startTask(id));
    }

    @GetMapping("/open")
    public ResponseEntity<List<MaintenanceTaskResponse>> getOpenTasks() {
        return ResponseEntity.ok(maintenanceTaskService.getOpenTasks());
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<MaintenanceTaskResponse>> getOverdueTasks() {
        return ResponseEntity.ok(maintenanceTaskService.getOverdueTasks());
    }

    @GetMapping("/machine/{machineId}")
    public ResponseEntity<List<MaintenanceTaskResponse>> getTasksByMachine(@PathVariable Long machineId) {
        return ResponseEntity.ok(maintenanceTaskService.getTasksByMachine(machineId));
    }

    @GetMapping("/technician/{technicianId}")
    public ResponseEntity<List<MaintenanceTaskResponse>> getTasksByTechnician(@PathVariable Long technicianId) {
        return ResponseEntity.ok(maintenanceTaskService.getTasksByTechnician(technicianId));
    }
}
