package machine.maintaninIt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "machines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Machine name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Machine code is required")
    @Column(nullable = false, unique = true)
    private String machineCode;

    @Column(length = 1000)
    private String description;

    @NotNull(message = "Maintenance interval hours is required")
    @Positive(message = "Maintenance interval hours must be greater than 0")
    @Column(nullable = false)
    private Long maintenanceIntervalHours;

    @PositiveOrZero(message = "Usage hours cannot be negative")
    @Column(nullable = false)
    private Long currentUsageHours = 0L;

    @PositiveOrZero(message = "Last maintenance usage hours cannot be negative")
    @Column(nullable = false)
    private Long lastMaintenanceUsageHours = 0L;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MachineStatus status = MachineStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsageLog> usageLogs = new ArrayList<>();

    @OneToMany(mappedBy = "machine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MaintenanceTask> maintenanceTasks = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
