package khoindn.swp391.be.app.exception.exceptions;

public class UserNotBelongException extends RuntimeException {
    public UserNotBelongException(String message) {
        super(message);
    }
}
