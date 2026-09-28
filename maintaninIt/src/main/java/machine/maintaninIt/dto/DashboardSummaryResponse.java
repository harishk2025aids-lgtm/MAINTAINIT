package machine.maintaninIt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {
    private long totalMachines;
    private long activeMachines;
    private long pendingMaintenance;
    private long overdueMaintenance;
    private long completedMaintenance;
    private long availableTechnicians;
}
