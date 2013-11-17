package epfl.sweng.exceptions;

/**
 * Should be thrown when the server responds with a 3xx or 4xx response status.
 * 
 * @author lseguy
 *
 */
public class BadRequestException extends ServerCommunicationException {
 
    private static final long serialVersionUID = 550199918339299193L;

    public BadRequestException() {
        super();
    }
    
    public BadRequestException(String message) {
        super(message);
    }
    
}
