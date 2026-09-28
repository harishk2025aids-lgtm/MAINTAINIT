package machine.maintaninIt.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, "Duplicate Resource", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ActiveMaintenanceTaskException.class)
    public ResponseEntity<ApiError> handleActiveMaintenanceTask(ActiveMaintenanceTaskException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, "Active Maintenance Task", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidTaskStatusException.class)
    public ResponseEntity<ApiError> handleInvalidTaskStatus(InvalidTaskStatusException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "Invalid Task Status", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidTechnicianException.class)
    public ResponseEntity<ApiError> handleInvalidTechnician(InvalidTechnicianException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "Invalid Technician", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(InvalidMaintenanceTaskException.class)
    public ResponseEntity<ApiError> handleInvalidMaintenanceTask(InvalidMaintenanceTaskException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "Invalid Maintenance Task", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (error instanceof FieldError fieldError) {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            } else {
                errors.put(error.getObjectName(), error.getDefaultMessage());
            }
        });
        return buildError(HttpStatus.BAD_REQUEST, "Validation Error", errors.toString(), request.getRequestURI());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, "Validation Error", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception ex, HttpServletRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage(), request.getRequestURI());
    }

    private ResponseEntity<ApiError> buildError(HttpStatus status, String error, String message, String path) {
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(error)
                .message(message)
                .path(path)
                .build();
        return new ResponseEntity<>(apiError, status);
    }
}
