package machine.maintaninIt.exception;

public class ActiveMaintenanceTaskException extends RuntimeException {
    public ActiveMaintenanceTaskException(String message) {
        super(message);
    }
}
