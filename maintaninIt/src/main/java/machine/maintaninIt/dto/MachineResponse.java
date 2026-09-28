package machine.maintaninIt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import machine.maintaninIt.entity.MachineStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineResponse {
    private Long id;
    private String name;
    private String machineCode;
    private String description;
    private Long maintenanceIntervalHours;
    private Long currentUsageHours;
    private Long lastMaintenanceUsageHours;
    private MachineStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
