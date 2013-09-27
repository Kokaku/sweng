package epfl.sweng.testing;

/// An exception that is thrown whenever TestingTransactions are used in a wrong
/// way.
public class TestingTransactionsError extends AssertionError {
	public TestingTransactionsError(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 1L;
}
