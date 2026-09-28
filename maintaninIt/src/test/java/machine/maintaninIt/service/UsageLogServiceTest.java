package machine.maintaninIt.service;

import machine.maintaninIt.dto.UsageLogRequest;
import machine.maintaninIt.dto.UsageLogResponse;
import machine.maintaninIt.entity.*;
import machine.maintaninIt.exception.InvalidMaintenanceTaskException;
import machine.maintaninIt.repository.MachineRepository;
import machine.maintaninIt.repository.MaintenanceTaskRepository;
import machine.maintaninIt.repository.UsageLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsageLogServiceTest {

    @Mock
    private UsageLogRepository usageLogRepository;

    @Mock
    private MachineRepository machineRepository;

    @Mock
    private MaintenanceTaskRepository maintenanceTaskRepository;

    @InjectMocks
    private UsageLogService usageLogService;

    @SuppressWarnings("null")
    @Test
    void createUsageLog_shouldUpdateMachineUsageAndCreateTask() {
        Machine machine = new Machine();
        machine.setId(1L);
        machine.setName("Compressor");
        machine.setMachineCode("CMP-01");
        machine.setMaintenanceIntervalHours(500L);
        machine.setCurrentUsageHours(0L);
        machine.setLastMaintenanceUsageHours(0L);
        machine.setStatus(MachineStatus.ACTIVE);

        UsageLogRequest request = new UsageLogRequest(1L, 500L, "Normal operation");

        when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));
        when(usageLogRepository.save(any(UsageLog.class))).thenAnswer(invocation -> {
            UsageLog log = invocation.getArgument(0);
            log.setId(10L);
            return log;
        });
        when(maintenanceTaskRepository.findByMachineAndStatusIn(eq(machine), anyList())).thenReturn(List.of());

        UsageLogResponse response = usageLogService.createUsageLog(request);

        assertNotNull(response);
        assertEquals(500L, machine.getCurrentUsageHours());
        verify(maintenanceTaskRepository).save(any(MaintenanceTask.class));
    }

    @SuppressWarnings("null")
    @Test
    void createUsageLog_shouldRejectDuplicateActiveTask() {
        Machine machine = new Machine();
        machine.setId(1L);
        machine.setName("Compressor");
        machine.setMachineCode("CMP-01");
        machine.setMaintenanceIntervalHours(500L);
        machine.setCurrentUsageHours(400L);
        machine.setLastMaintenanceUsageHours(0L);

        when(machineRepository.findById(1L)).thenReturn(Optional.of(machine));
        when(usageLogRepository.save(any(UsageLog.class))).thenAnswer(invocation -> {
            UsageLog log = invocation.getArgument(0, UsageLog.class);
            return log;
        });
        when(maintenanceTaskRepository.findByMachineAndStatusIn(eq(machine), anyList())).thenReturn(List.of(new MaintenanceTask()));

        UsageLogRequest request = new UsageLogRequest(1L, 150L, "push beyond interval");

        assertThrows(InvalidMaintenanceTaskException.class, () -> usageLogService.createUsageLog(request));
    }
}
