package epfl.sweng.exceptions;

/**
 * Should be thrown when a network or local request is unsuccessful.
 * 
 * @author lseguy
 *
 */
public class CommunicationException extends Exception {
    
    private static final long serialVersionUID = 6294494153759710260L;

    public CommunicationException() {
        super();
    }
    
    public CommunicationException(String message) {
        super(message);
    }
    
}
