package exception;

public class CommentaireNotFoundException extends RuntimeException {
    public CommentaireNotFoundException(String message) {
        super(message);
    }
}
