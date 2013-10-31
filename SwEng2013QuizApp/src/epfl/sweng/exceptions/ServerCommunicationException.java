/**
 * 
 */
package epfl.sweng.exceptions;

/**
 * Should be thrown when a network request is unsuccessful.
 * 
 * For instance can be thrown instead of an ExecutionException thrown by an
 * {@link AsyncTask}
 * 
 * @author lseguy
 *
 */
public class ServerCommunicationException extends Exception {
 
    private static final long serialVersionUID = 1171644294660709870L;

    public ServerCommunicationException() {
        super();
    }
    
    public ServerCommunicationException(String message) {
        super(message);
    }
    
}
