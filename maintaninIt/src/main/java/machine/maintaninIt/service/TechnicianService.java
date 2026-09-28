package machine.maintaninIt.service;

import machine.maintaninIt.dto.TechnicianRequest;
import machine.maintaninIt.dto.TechnicianResponse;
import machine.maintaninIt.entity.Technician;
import machine.maintaninIt.entity.TechnicianStatus;
import machine.maintaninIt.exception.DuplicateResourceException;
import machine.maintaninIt.exception.ResourceNotFoundException;
import machine.maintaninIt.repository.TechnicianRepository;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class TechnicianService {

    private final TechnicianRepository technicianRepository;

    public TechnicianService(TechnicianRepository technicianRepository) {
        this.technicianRepository = technicianRepository;
    }

    public TechnicianResponse createTechnician(TechnicianRequest request) {
        if (technicianRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Technician with email '" + request.getEmail() + "' already exists");
        }

        Technician technician = new Technician();
        technician.setName(request.getName());
        technician.setEmail(request.getEmail());
        technician.setPhone(request.getPhone());
        technician.setSpecialization(request.getSpecialization());
        technician.setStatus(request.getStatus() != null ? request.getStatus() : TechnicianStatus.AVAILABLE);

        Technician saved = technicianRepository.save(technician);
        return mapToResponse(saved);
    }

    public List<TechnicianResponse> getAllTechnicians(String status) {
        if (status != null && !status.isBlank()) {
            TechnicianStatus technicianStatus = TechnicianStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
            return technicianRepository.findByStatus(technicianStatus).stream().map(this::mapToResponse).toList();
        }
        return technicianRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public TechnicianResponse getTechnicianById(@NonNull Long id) {
        return mapToResponse(findTechnicianById(id));
    }

    @SuppressWarnings("null")
    public TechnicianResponse updateTechnician(Long id, TechnicianRequest request) {
        Technician technician = findTechnicianById(id);

        if (!technician.getEmail().equals(request.getEmail()) && technicianRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Technician with email '" + request.getEmail() + "' already exists");
        }

        technician.setName(request.getName());
        technician.setEmail(request.getEmail());
        technician.setPhone(request.getPhone());
        technician.setSpecialization(request.getSpecialization());
        technician.setStatus(request.getStatus() != null ? request.getStatus() : technician.getStatus());

        return mapToResponse(technicianRepository.save(technician));
    }

    @SuppressWarnings("null")
    public void deleteTechnician(@NonNull Long id) {
        technicianRepository.delete(findTechnicianById(id));
    }

    public @Nullable Technician findTechnicianById(@NonNull Long id) {
        return technicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technician with id " + id + " was not found"));
    }

    public List<Technician> getAvailableTechnicians() {
        return technicianRepository.findByStatus(TechnicianStatus.AVAILABLE);
    }

    public TechnicianResponse mapToResponse(Technician technician) {
        TechnicianResponse response = new TechnicianResponse();
        response.setId(technician.getId());
        response.setName(technician.getName());
        response.setEmail(technician.getEmail());
        response.setPhone(technician.getPhone());
        response.setSpecialization(technician.getSpecialization());
        response.setStatus(technician.getStatus());
        return response;
    }
}
