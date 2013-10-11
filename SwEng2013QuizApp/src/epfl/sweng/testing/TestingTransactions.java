package epfl.sweng.testing;

import android.app.Instrumentation;
import android.util.Log;

/**
 * A transaction used in unit testing. This class provides a mechanism to signal
 * to a test driver when a request has finished processing. This allows the test
 * driver to initiate a request (e.g., by clicking on a button) and then wait
 * until all processing has been done (e.g., network requests completed and the
 * UI has been updated), and then verify that the result is OK.
 * 
 * We require all SwEng projects to support TestingTransactions. This allows us
 * to create reliable and quick blackbox tests.
 * 
 * Usage:
 * 
 * In the test driver, call TestingTransactions.run(getInstrumentation(), new
 * TestingTransaction() { // override the initiate and verify methods, and
 * probably toString });
 * 
 * In the student code, call
 * TestingTransactions.check(TTChecks.THE_THING_I_JUST_DID) whenever a request
 * is completely done.
 */
public final class TestingTransactions {
	private static final TestingTransactions INSTANCE = new TestingTransactions();

	/** The tag used for logging */
	private static final String TAG = "TestingTransaction";

	/** The maximum time we wait for a transaction to complete, in milliseconds */
	private static final long TRANSACTION_TIMEOUT = 5500;

	/** The time when the current transaction was started */
	private long startTime;

	/** The possible states of a transaction */
	private enum TTState {
		IDLE, INITIATED, COMPLETED
	};
	private TTState state = TTState.IDLE;

	/**
	 * The possible points at which transactions end. Each of these corresponds
	 * to some finished action.
	 */
	public enum TTChecks {
		NONE,
		QUESTION_SHOWN,
		ANSWER_SELECTED,
		QUIZ_SCORE_SHOWN,
		AVAILABLE_QUIZZES_SHOWN,
		MAIN_ACTIVITY_SHOWN,
		EDIT_QUESTIONS_SHOWN,
		QUESTION_EDITED,
		NEW_QUESTION_SUBMITTED,
		AUTHENTICATION_ACTIVITY_SHOWN,
		LOGGED_OUT
	};

	private TTChecks currentCheck = TTChecks.NONE;

	// / Runs the given TestingTransaction
	public static void run(Instrumentation instr, TestingTransaction t) {
		TestingTransactions tts = TestingTransactions.getInstance();
		TTChecks receivedCheck = null;
		
		try {
			// 1) initiate the transaction
			synchronized (tts) {
				if (tts.state != TTState.IDLE) {
					throw new TestingTransactionsError(
							"Attempt to run transaction '" + t +
							"', but another transaction is running.");
				}
				tts.startTime = System.currentTimeMillis();
				Log.d(TAG, String.format("Starting transaction %s", t));
	
				tts.state = TTState.INITIATED;
			}
	
			// We give up our lock while initiating the transaction. There are now
			// two cases:
			// 1) The transaction completes immediately. In that case, the state
			//    will be set to COMPLETED, and we'll never wait.
			// 2) The transaction does not complete immediately. In that case, we'll
			//    call tts.wait() to wait for completion.
			t.initiate();
	
			synchronized (tts) {
				// If the transaction is not initiated or completed, this
				// probably means that another transaction was run
				// simultaneously, and set the state back to IDLE.
				if (tts.state != TTState.INITIATED && tts.state != TTState.COMPLETED) {
					throw new TestingTransactionsError(
							"Attempt to wait for transaction '" + t +
							"', but it was aborted.");
				}
				
				// 2) wait for the transaction to complete (i.e., to call check)
				long currentTime = System.currentTimeMillis();
				while (tts.state != TTState.COMPLETED
						&& currentTime < tts.startTime + TRANSACTION_TIMEOUT) {
					try {
						Log.d(TAG, String.format("Waiting for transaction %s...", t));
						tts.wait(TRANSACTION_TIMEOUT - (currentTime - tts.startTime));
						Log.d(TAG, String.format("Waiting for transaction %s... done", t));
					} catch (InterruptedException e) {
						// Nothing...
					}
					currentTime = System.currentTimeMillis();
				}
	
				if (tts.state != TTState.COMPLETED) {
					throw new TestingTransactionsError(String.format(
							"Transaction %s timed out (elapsed: %d)",
							t, currentTime - tts.startTime));
				}
				receivedCheck = tts.currentCheck;
			}
	
			// 3) verify the result of the transaction
			// Again, we give up our lock for this, because otherwise a deadlock
			// can occur if the student code calls check() during the
			// waitForIdleSync().
			instr.waitForIdleSync();
			t.verify(receivedCheck);
			Log.d(TAG, String.format(
					"Completed transaction %s (elapsed: %d)",
					t, System.currentTimeMillis() - tts.startTime));
		} finally {
			synchronized (tts) {
				tts.state = TTState.IDLE;
			}
		}
	}

	// Notifies the waiting thread that the transaction has been completed and
	// is ready to be verified
	public static void check(TTChecks completedCheck) {
		Log.d(TAG, String.format("TestingTransactions.check(%s)", completedCheck));
		TestingTransactions tts = TestingTransactions.getInstance();
		synchronized (tts) {
			if (tts.state == TTState.IDLE) {
				return; // Do nothing if we're not in testing mode
			} else if (tts.state == TTState.INITIATED) {
				tts.state = TTState.COMPLETED;
				tts.currentCheck = completedCheck;
				tts.notify();
			} else {
				throw new TestingTransactionsError(String.format(
						"Multiple calls to check: First was %s, then %s",
						tts.currentCheck, completedCheck));
			}
		}
	}

	// Singleton private constructor
	private TestingTransactions() {
	}

	// Retrieve the singleton instance of TestingTransaction
	private static TestingTransactions getInstance() {
		return INSTANCE;
	}
}
