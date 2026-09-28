package machine.maintaninIt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import machine.maintaninIt.entity.TechnicianStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String specialization;
    private TechnicianStatus status;
}
