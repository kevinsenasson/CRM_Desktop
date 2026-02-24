package exception;

public class EnterpriseNotFoundException extends RuntimeException {
    public EnterpriseNotFoundException(String message) {
        super(message);
    }
}
