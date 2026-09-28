package machine.maintaninIt.service;

import machine.maintaninIt.dto.MachineRequest;
import machine.maintaninIt.dto.MachineResponse;
import machine.maintaninIt.entity.Machine;
import machine.maintaninIt.entity.MachineStatus;
import machine.maintaninIt.exception.DuplicateResourceException;
import machine.maintaninIt.exception.ResourceNotFoundException;
import machine.maintaninIt.repository.MachineRepository;
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
class MachineServiceTest {

    @Mock
    private MachineRepository machineRepository;

    @InjectMocks
    private MachineService machineService;

    @Test
    void createMachine_shouldSaveMachine() {
        MachineRequest request = new MachineRequest("Drill Press", "DP-100", "Main production drill", 500L, 0L, 0L, MachineStatus.ACTIVE);
        Machine machine = new Machine();
        machine.setId(1L);
        machine.setName(request.getName());
        machine.setMachineCode(request.getMachineCode());
        machine.setMaintenanceIntervalHours(request.getMaintenanceIntervalHours());
        machine.setCurrentUsageHours(request.getCurrentUsageHours());
        machine.setLastMaintenanceUsageHours(request.getLastMaintenanceUsageHours());
        machine.setStatus(request.getStatus());

        when(machineRepository.findByMachineCode("DP-100")).thenReturn(Optional.empty());
        when(machineRepository.save(any(Machine.class))).thenReturn(machine);

        MachineResponse response = machineService.createMachine(request);

        assertNotNull(response);
        assertEquals("DP-100", response.getMachineCode());
        verify(machineRepository).save(any(Machine.class));
    }

    @Test
    void createMachine_shouldThrowWhenDuplicateCode() {
        MachineRequest request = new MachineRequest("Drill Press", "DP-100", "Test", 500L, 0L, 0L, MachineStatus.ACTIVE);
        when(machineRepository.findByMachineCode("DP-100")).thenReturn(Optional.of(new Machine()));

        assertThrows(DuplicateResourceException.class, () -> machineService.createMachine(request));
    }

    @Test
    void getMachineById_shouldThrowWhenNotFound() {
        when(machineRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> machineService.getMachineById(99L));
    }

    @Test
    void getAllMachines_shouldReturnList() {
        Machine machine = new Machine();
        machine.setId(1L);
        machine.setName("Pump");
        machine.setMachineCode("P-1");
        machine.setMaintenanceIntervalHours(200L);
        machine.setCurrentUsageHours(100L);
        machine.setLastMaintenanceUsageHours(0L);
        machine.setStatus(MachineStatus.ACTIVE);

        when(machineRepository.findAll()).thenReturn(List.of(machine));

        List<MachineResponse> responses = machineService.getAllMachines(null, null, null);

        assertEquals(1, responses.size());
        assertEquals("P-1", responses.get(0).getMachineCode());
    }
}
