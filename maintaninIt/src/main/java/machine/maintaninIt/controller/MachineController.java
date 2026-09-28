package machine.maintaninIt.controller;

import jakarta.validation.Valid;
import machine.maintaninIt.dto.MachineRequest;
import machine.maintaninIt.dto.MachineResponse;
import machine.maintaninIt.service.MachineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/machines")
public class MachineController {

    private final MachineService machineService;

    public MachineController(MachineService machineService) {
        this.machineService = machineService;
    }

    @PostMapping
    public ResponseEntity<MachineResponse> createMachine(@Valid @RequestBody MachineRequest request) {
        return new ResponseEntity<>(machineService.createMachine(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<MachineResponse>> getAllMachines(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String machineCode,
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(machineService.getAllMachines(status, machineCode, name));
    }

    @SuppressWarnings("null")
    @GetMapping("/{id}")
    public ResponseEntity<MachineResponse> getMachineById(@PathVariable Long id) {
        return ResponseEntity.ok(machineService.getMachineById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MachineResponse> updateMachine(@PathVariable @NonNull Long id, @Valid @RequestBody MachineRequest request) {
        return ResponseEntity.ok(machineService.updateMachine(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMachine(@PathVariable Long id) {
        machineService.deleteMachine(id);
        return ResponseEntity.noContent().build();
    }
}
