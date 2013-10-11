package epfl.sweng.testing;

/**
 * A TestingTransaction encapsulates a complete action that happens in a
 * program. It is initiated, for example through a click on a button. The action
 * then causes some processing to happen, and ends when the state of the
 * application has been updated. Once the transaction completed, the resulting
 * application state can be verified for consistency and correctness.
 */
public interface TestingTransaction {
	
	/**
	 * Contains the code to initiate the transaction. For example, this might
	 * press a button on the user interface.
	 */
	void initiate();
	
	/**
	 * Contains the code to verify that a transaction succeeded. For example,
	 * this might check that the user interface has been updated correctly after
	 * a button has been pressed.
	 * 
	 * @param completedCheck
	 *            : The notification that was passed to check()
	 */
	void verify(TestingTransactions.TTChecks completedCheck);
}
