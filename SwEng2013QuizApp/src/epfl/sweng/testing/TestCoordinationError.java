package epfl.sweng.testing;

/**
 * An exception that is thrown whenever the TestCoordinator detects an
 * inconsistency.
 */
public class TestCoordinationError extends AssertionError {
    public TestCoordinationError(String msg) {
        super(msg);
    }

    private static final long serialVersionUID = 1L;
}
