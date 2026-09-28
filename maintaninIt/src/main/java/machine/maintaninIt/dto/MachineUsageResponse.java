package machine.maintaninIt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MachineUsageResponse {
    private Long machineId;
    private String machineCode;
    private String name;
    private Long currentUsageHours;
    private Long lastMaintenanceUsageHours;
    private Long maintenanceIntervalHours;
}
