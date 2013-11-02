package epfl.sweng.exceptions;

/**
 * Should be thrown when authentication is unsuccessful because of wrong
 * credentials.
 * 
 * @author lseguy
 *
 */
public class InvalidCredentialsException extends ServerCommunicationException {

    private static final long serialVersionUID = 3636732404000852900L;

    public InvalidCredentialsException() {
        super();
    }
    
    public InvalidCredentialsException(String message) {
        super(message);
    }
    
}

