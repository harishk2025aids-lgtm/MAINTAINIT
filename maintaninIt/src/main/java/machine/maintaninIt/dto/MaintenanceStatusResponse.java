package machine.maintaninIt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import machine.maintaninIt.entity.MachineStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceStatusResponse {
    private Long machineId;
    private String machineCode;
    private String name;
    private MachineStatus machineStatus;
    private Long currentUsageHours;
    private Long maintenanceIntervalHours;
    private Long nextMaintenanceUsageHours;
    private boolean maintenanceDue;
}
