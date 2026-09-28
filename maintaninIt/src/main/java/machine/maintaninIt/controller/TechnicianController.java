package machine.maintaninIt.controller;

import jakarta.validation.Valid;
import machine.maintaninIt.dto.TechnicianRequest;
import machine.maintaninIt.dto.TechnicianResponse;
import machine.maintaninIt.service.TechnicianService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technicians")
public class TechnicianController {

    private final TechnicianService technicianService;

    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    @PostMapping
    public ResponseEntity<TechnicianResponse> createTechnician(@Valid @RequestBody TechnicianRequest request) {
        return new ResponseEntity<>(technicianService.createTechnician(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TechnicianResponse>> getAllTechnicians(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(technicianService.getAllTechnicians(status));
    }

    @SuppressWarnings("null")
    @GetMapping("/{id}")
    public ResponseEntity<TechnicianResponse> getTechnicianById(@PathVariable Long id) {
        return ResponseEntity.ok(technicianService.getTechnicianById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TechnicianResponse> updateTechnician(@PathVariable Long id, @Valid @RequestBody TechnicianRequest request) {
        return ResponseEntity.ok(technicianService.updateTechnician(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTechnician(@PathVariable @NonNull Long id) {
        technicianService.deleteTechnician(id);
        return ResponseEntity.noContent().build();
    }
}
