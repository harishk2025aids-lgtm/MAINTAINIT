package machine.maintaninIt.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "usage_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Machine is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", nullable = false)
    private Machine machine;

    @NotNull(message = "Usage hours is required")
    @PositiveOrZero(message = "Usage hours cannot be negative")
    @Column(nullable = false)
    private Long usageHours;

    @Column(nullable = false)
    private LocalDateTime loggedAt;

    @Column(length = 1000)
    private String remarks;

    @PrePersist
    protected void onCreate() {
        this.loggedAt = LocalDateTime.now();
    }
}
