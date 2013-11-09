package epfl.sweng.exceptions;

/**
 * Should be thrown when a DB request is unsuccessful.
 * 
 * @author lseguy
 *
 */
public class DBException extends Exception {
 
    private static final long serialVersionUID = 4908683393920404260L;

    public DBException() {
        super();
    }
    
    public DBException(String message) {
        super(message);
    }
    
}
