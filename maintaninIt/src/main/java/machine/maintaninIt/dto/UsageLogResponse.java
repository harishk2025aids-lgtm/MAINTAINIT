package machine.maintaninIt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageLogResponse {
    private Long id;
    private Long machineId;
    private Long usageHours;
    private LocalDateTime loggedAt;
    private String remarks;
}
