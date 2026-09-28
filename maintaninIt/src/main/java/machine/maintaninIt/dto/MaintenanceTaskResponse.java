package machine.maintaninIt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import machine.maintaninIt.entity.MaintenancePriority;
import machine.maintaninIt.entity.MaintenanceTaskStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceTaskResponse {
    private Long id;
    private Long machineId;
    private Long technicianId;
    private String taskTitle;
    private String description;
    private Long scheduledUsageHours;
    private MaintenanceTaskStatus status;
    private MaintenancePriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String notes;
}
