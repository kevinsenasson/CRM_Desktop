package exception;

public class CommercialNotFoundException extends RuntimeException {
    public CommercialNotFoundException(String message) {
        super(message);
    }
}
