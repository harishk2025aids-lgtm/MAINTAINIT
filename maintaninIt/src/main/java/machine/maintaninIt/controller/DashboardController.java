package machine.maintaninIt.controller;

import machine.maintaninIt.dto.DashboardSummaryResponse;
import machine.maintaninIt.dto.MachineUsageResponse;
import machine.maintaninIt.dto.MaintenanceNotificationResponse;
import machine.maintaninIt.dto.MaintenanceStatusResponse;
import machine.maintaninIt.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }

    @GetMapping("/maintenance-status")
    public ResponseEntity<List<MaintenanceStatusResponse>> getMaintenanceStatus() {
        return ResponseEntity.ok(dashboardService.getMaintenanceStatus());
    }

    @GetMapping("/machine-usage")
    public ResponseEntity<List<MachineUsageResponse>> getMachineUsage() {
        return ResponseEntity.ok(dashboardService.getMachineUsage());
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<MaintenanceNotificationResponse>> getNotifications() {
        return ResponseEntity.ok(dashboardService.getNotifications());
    }
}
