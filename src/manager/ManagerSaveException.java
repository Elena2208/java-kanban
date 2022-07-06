package manager;

public class ManagerSaveException extends RuntimeException {
    private final String message = "";

    public ManagerSaveException(String message) {
        super(message);
    }
}
