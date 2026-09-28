package machine.maintaninIt.service;

import machine.maintaninIt.dto.TechnicianRequest;
import machine.maintaninIt.dto.TechnicianResponse;
import machine.maintaninIt.entity.Technician;
import machine.maintaninIt.entity.TechnicianStatus;
import machine.maintaninIt.exception.DuplicateResourceException;
import machine.maintaninIt.exception.ResourceNotFoundException;
import machine.maintaninIt.repository.TechnicianRepository;
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
class TechnicianServiceTest {

    @Mock
    private TechnicianRepository technicianRepository;

    @InjectMocks
    private TechnicianService technicianService;

    @SuppressWarnings("null")
    @Test
    void createTechnician_shouldSaveTechnician() {
        TechnicianRequest request = new TechnicianRequest("John Doe", "john@email.com", "123456789", "Electrical", TechnicianStatus.AVAILABLE);
        Technician technician = new Technician();
        technician.setId(1L);
        technician.setName(request.getName());
        technician.setEmail(request.getEmail());
        technician.setPhone(request.getPhone());
        technician.setSpecialization(request.getSpecialization());
        technician.setStatus(request.getStatus());

        when(technicianRepository.findByEmail("john@email.com")).thenReturn(Optional.empty());
        when(technicianRepository.save(any(Technician.class))).thenReturn(technician);

        TechnicianResponse response = technicianService.createTechnician(request);

        assertNotNull(response);
        assertEquals("john@email.com", response.getEmail());
        assertEquals(TechnicianStatus.AVAILABLE, response.getStatus());
    }

    @Test
    void createTechnician_shouldThrowWhenDuplicateEmail() {
        TechnicianRequest request = new TechnicianRequest("John Doe", "john@email.com", "123456789", "Electrical", TechnicianStatus.AVAILABLE);
        when(technicianRepository.findByEmail("john@email.com")).thenReturn(Optional.of(new Technician()));

        assertThrows(DuplicateResourceException.class, () -> technicianService.createTechnician(request));
    }

    @Test
    void getTechnicianById_shouldThrowWhenNotFound() {
        when(technicianRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> technicianService.getTechnicianById(4L));
    }

    @Test
    void getAllTechnicians_shouldReturnList() {
        Technician technician = new Technician();
        technician.setId(1L);
        technician.setName("Mary");
        technician.setEmail("mary@email.com");
        technician.setPhone("1111111");
        technician.setSpecialization("Mechanical");
        technician.setStatus(TechnicianStatus.AVAILABLE);

        when(technicianRepository.findAll()).thenReturn(List.of(technician));

        List<TechnicianResponse> responses = technicianService.getAllTechnicians(null);

        assertEquals(1, responses.size());
        assertEquals("mary@email.com", responses.get(0).getEmail());
    }
}
