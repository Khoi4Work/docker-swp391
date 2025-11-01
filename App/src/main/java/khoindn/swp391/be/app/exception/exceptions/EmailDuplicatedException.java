package khoindn.swp391.be.app.exception.exceptions;

public class EmailDuplicatedException extends IllegalArgumentException {
    public EmailDuplicatedException(String message) {
        super(message);
    }
}
