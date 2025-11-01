package khoindn.swp391.be.app.exception.exceptions;

public class UnauthorizedAccessException extends  RuntimeException{
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
