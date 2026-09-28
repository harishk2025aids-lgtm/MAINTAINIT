package machine.maintaninIt.service;

import machine.maintaninIt.dto.MachineRequest;
import machine.maintaninIt.dto.MachineResponse;
import machine.maintaninIt.entity.Machine;
import machine.maintaninIt.entity.MachineStatus;
import machine.maintaninIt.exception.DuplicateResourceException;
import machine.maintaninIt.exception.ResourceNotFoundException;
import machine.maintaninIt.repository.MachineRepository;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class MachineService {

    private final MachineRepository machineRepository;

    public MachineService(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    public MachineResponse createMachine(MachineRequest request) {
        if (machineRepository.findByMachineCode(request.getMachineCode()).isPresent()) {
            throw new DuplicateResourceException("Machine with code '" + request.getMachineCode() + "' already exists");
        }

        Machine machine = new Machine();
        machine.setName(request.getName());
        machine.setMachineCode(request.getMachineCode());
        machine.setDescription(request.getDescription());
        machine.setMaintenanceIntervalHours(request.getMaintenanceIntervalHours());
        machine.setCurrentUsageHours(request.getCurrentUsageHours() != null ? request.getCurrentUsageHours() : 0L);
        machine.setLastMaintenanceUsageHours(request.getLastMaintenanceUsageHours() != null ? request.getLastMaintenanceUsageHours() : 0L);
        machine.setStatus(request.getStatus() != null ? request.getStatus() : MachineStatus.ACTIVE);

        Machine savedMachine = machineRepository.save(machine);
        return mapToResponse(savedMachine);
    }

    public List<MachineResponse> getAllMachines(String status, String machineCode, String name) {
        if (status != null && !status.isBlank()) {
            MachineStatus machineStatus = MachineStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
            return machineRepository.findByStatus(machineStatus).stream().map(this::mapToResponse).toList();
        }

        if (machineCode != null && !machineCode.isBlank()) {
            return machineRepository.findByMachineCodeContainingIgnoreCase(machineCode.trim()).stream().map(this::mapToResponse).toList();
        }

        if (name != null && !name.isBlank()) {
            return machineRepository.findByNameContainingIgnoreCase(name.trim()).stream().map(this::mapToResponse).toList();
        }

        return machineRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public MachineResponse getMachineById(@NonNull Long id) {
        Machine machine = findMachineById(id);
        return mapToResponse(machine);
    }

    public MachineResponse updateMachine(@NonNull Long id, MachineRequest request) {
        Machine machine = findMachineById(id);

        if (!machine.getMachineCode().equals(request.getMachineCode()) &&
                machineRepository.findByMachineCode(request.getMachineCode()).isPresent()) {
            throw new DuplicateResourceException("Machine with code '" + request.getMachineCode() + "' already exists");
        }

        machine.setName(request.getName());
        machine.setMachineCode(request.getMachineCode());
        machine.setDescription(request.getDescription());
        machine.setMaintenanceIntervalHours(request.getMaintenanceIntervalHours());
        machine.setCurrentUsageHours(request.getCurrentUsageHours() != null ? request.getCurrentUsageHours() : machine.getCurrentUsageHours());
        machine.setLastMaintenanceUsageHours(request.getLastMaintenanceUsageHours() != null ? request.getLastMaintenanceUsageHours() : machine.getLastMaintenanceUsageHours());
        machine.setStatus(request.getStatus() != null ? request.getStatus() : machine.getStatus());

        Machine updatedMachine = machineRepository.save(machine);
        return mapToResponse(updatedMachine);
    }

    @SuppressWarnings("null")
    public void deleteMachine(Long id) {
        Machine machine = findMachineById(id);
        machineRepository.delete(machine);
    }

    public Machine findMachineById(@NonNull Long id) {
        return machineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Machine with id " + id + " was not found"));
    }

    public MachineResponse mapToResponse(Machine machine) {
        MachineResponse response = new MachineResponse();
        response.setId(machine.getId());
        response.setName(machine.getName());
        response.setMachineCode(machine.getMachineCode());
        response.setDescription(machine.getDescription());
        response.setMaintenanceIntervalHours(machine.getMaintenanceIntervalHours());
        response.setCurrentUsageHours(machine.getCurrentUsageHours());
        response.setLastMaintenanceUsageHours(machine.getLastMaintenanceUsageHours());
        response.setStatus(machine.getStatus());
        response.setCreatedAt(machine.getCreatedAt());
        response.setUpdatedAt(machine.getUpdatedAt());
        return response;
    }
}
