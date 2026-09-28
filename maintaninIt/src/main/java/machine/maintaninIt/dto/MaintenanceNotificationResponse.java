package machine.maintaninIt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceNotificationResponse {
    private String type;
    private String machineName;
    private String machineCode;
    private String message;
    private long remainingHours;
    private long nextMaintenanceUsageHours;
}
