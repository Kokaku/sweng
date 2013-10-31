/**
 * 
 */
package epfl.sweng.exceptions;

/**
 * Should be thrown when the user is expected to be logged in but is not.
 * 
 * @author lseguy
 *
 */
public class NotLoggedInException extends Exception {
 
    private static final long serialVersionUID = 496830678508983093L;

    public NotLoggedInException() {
        super();
    }
    
    public NotLoggedInException(String message) {
        super(message);
    }
    
}
