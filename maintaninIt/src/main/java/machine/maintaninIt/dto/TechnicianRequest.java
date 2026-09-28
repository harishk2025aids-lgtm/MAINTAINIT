package machine.maintaninIt.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import machine.maintaninIt.entity.TechnicianStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechnicianRequest {

    @NotBlank(message = "Technician name is required")
    private String name;

    @NotBlank(message = "Technician email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone is required")
    @Size(min = 7, max = 20, message = "Phone number length must be between 7 and 20")
    private String phone;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @NotNull(message = "Technician status is required")
    private TechnicianStatus status = TechnicianStatus.AVAILABLE;
}
