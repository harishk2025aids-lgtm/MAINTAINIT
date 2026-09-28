package machine.maintaninIt.controller;

import jakarta.validation.Valid;
import machine.maintaninIt.dto.UsageLogRequest;
import machine.maintaninIt.dto.UsageLogResponse;
import machine.maintaninIt.service.UsageLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usage-logs")
public class UsageLogController {

    private final UsageLogService usageLogService;

    public UsageLogController(UsageLogService usageLogService) {
        this.usageLogService = usageLogService;
    }

    @PostMapping
    public ResponseEntity<UsageLogResponse> createUsageLog(@Valid @RequestBody UsageLogRequest request) {
        return new ResponseEntity<>(usageLogService.createUsageLog(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<UsageLogResponse>> getAllUsageLogs() {
        return ResponseEntity.ok(usageLogService.getAllUsageLogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsageLogResponse> getUsageLogById(@PathVariable Long id) {
        return ResponseEntity.ok(usageLogService.getUsageLogById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsageLogResponse> updateUsageLog(@PathVariable Long id,
                                                            @Valid @RequestBody UsageLogRequest request) {
        return ResponseEntity.ok(usageLogService.updateUsageLog(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsageLog(@PathVariable Long id) {
        usageLogService.deleteUsageLog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/machine/{machineId}")
    public ResponseEntity<List<UsageLogResponse>> getUsageLogsByMachine(@PathVariable Long machineId) {
        return ResponseEntity.ok(usageLogService.getUsageLogsByMachine(machineId));
    }
}
