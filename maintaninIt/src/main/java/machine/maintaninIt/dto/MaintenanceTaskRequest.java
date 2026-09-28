package machine.maintaninIt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import machine.maintaninIt.entity.MaintenancePriority;
import machine.maintaninIt.entity.MaintenanceTaskStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceTaskRequest {

    @NotNull(message = "Machine id is required")
    private Long machineId;

    private Long technicianId;

    @NotBlank(message = "Task title is required")
    private String taskTitle;

    private String description;

    @NotNull(message = "Scheduled usage hours is required")
    @Positive(message = "Scheduled usage hours must be positive")
    private Long scheduledUsageHours;

    private MaintenanceTaskStatus status = MaintenanceTaskStatus.OPEN;

    private MaintenancePriority priority = MaintenancePriority.MEDIUM;

    private String notes;
}
