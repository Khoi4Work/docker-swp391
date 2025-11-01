package khoindn.swp391.be.app.exception.exceptions;

public class PastDateBookingException extends RuntimeException {
    public PastDateBookingException(String message) {
        super(message);
    }
}
