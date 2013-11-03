package epfl.sweng.exceptions;

/**
 * Should be thrown when a DB request is unsuccessful.
 * 
 * @author lseguy
 *
 */
public class DBCommunicationException extends CommunicationException {
 
    private static final long serialVersionUID = 4908683393920404260L;

    public DBCommunicationException() {
        super();
    }
    
    public DBCommunicationException(String message) {
        super(message);
    }
    
}
