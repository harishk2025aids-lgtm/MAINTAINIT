package machine.maintaninIt.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageLogRequest {

    @NotNull(message = "Machine id is required")
    private Long machineId;

    @NotNull(message = "Usage hours is required")
    @PositiveOrZero(message = "Usage hours cannot be negative")
    private Long usageHours;

    private String remarks;
}
