package khoindn.swp391.be.app.exception.exceptions;

public class UserIsExistedException extends RuntimeException {
    public UserIsExistedException(String message) {
        super(message);
    }
}
