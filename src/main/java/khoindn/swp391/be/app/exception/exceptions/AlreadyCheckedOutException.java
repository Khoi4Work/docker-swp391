package khoindn.swp391.be.app.exception.exceptions;

public class AlreadyCheckedOutException extends RuntimeException {
    public AlreadyCheckedOutException(String message) {
        super(message);
    }
}
