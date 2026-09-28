package machine.maintaninIt.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import machine.maintaninIt.entity.MachineStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MachineRequest {

    @NotBlank(message = "Machine name is required")
    private String name;

    @NotBlank(message = "Machine code is required")
    private String machineCode;

    private String description;

    @NotNull(message = "Maintenance interval hours is required")
    @Positive(message = "Maintenance interval must be positive")
    private Long maintenanceIntervalHours;

    @PositiveOrZero(message = "Current usage hours cannot be negative")
    private Long currentUsageHours = 0L;

    @PositiveOrZero(message = "Last maintenance usage hours cannot be negative")
    private Long lastMaintenanceUsageHours = 0L;

    private MachineStatus status = MachineStatus.ACTIVE;
}
