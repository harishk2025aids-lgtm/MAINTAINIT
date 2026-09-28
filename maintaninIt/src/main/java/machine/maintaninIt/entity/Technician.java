package machine.maintaninIt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "technicians")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Technician {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Technician name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Technician email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Phone is required")
    @Size(min = 7, max = 20, message = "Phone number length must be between 7 and 20")
    @Column(nullable = false)
    private String phone;

    @NotBlank(message = "Specialization is required")
    @Column(nullable = false)
    private String specialization;

    @NotNull(message = "Technician status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TechnicianStatus status = TechnicianStatus.AVAILABLE;

    @OneToMany(mappedBy = "technician")
    private List<MaintenanceTask> maintenanceTasks = new ArrayList<>();
}
