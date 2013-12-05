package epfl.sweng.testing;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.util.Log;

/**
 * epfl.sweng.testing.Toast is a drop-in replacement for android.widget.Toast.
 * It contains much of the same functionality, but is more testable. The
 * differences to the standard toast class are:
 * 
 * - Multiple toasts are displayed simultaneously in the same view. Messages do
 *   not queue up as they do for regular toasts.
 * - There is a static cancelAllToasts method that removes all the toasts from
 *   the screen. This can be used for example by the TestCoordinator, to clean
 *   up after a test is done.
 * - Using waitForToastsToBeUpdated, tests can ensure that toasts are
 *   displayed/hidden properly before executing checks.
 */
public final class Toast {

    public static final int LENGTH_SHORT = 0;
    public static final int LENGTH_LONG  = 1;
    
    private static final int TOAST_DELAY = 3500; // 3.5 seconds
    private static final int POLLING_SLEEP_TIME = 10;
    
    /** A helper class to cancel a toast */
    private class CancelToastRunnable implements Runnable {

        private Toast toast;

        public CancelToastRunnable(Toast t) {
            this.toast = t;
        }
        
        @Override
        public void run() {
            toast.cancel();
        }
    }

    private static Set<Toast> allToasts = new HashSet<Toast>();
    private static final ScheduledThreadPoolExecutor TIMER = new ScheduledThreadPoolExecutor(0);

    private static android.widget.Toast wrappedToast;
    private static Context context;
    
    private final String text;
    private ScheduledFuture<?> scheduledFuture;
    
    private Toast(String t) {
        this.text = t;
    }
    
    /** Make a standard toast that just contains a text view with the text from a resource.
     *  The duration parameter is ignored. */
    public static Toast makeText(Context ctx, int resId, int duration) {
        createWrappedToast(ctx);
        return new Toast(ctx.getString(resId));
    }
    
    /** Make a standard toast that just contains a text view.
     *  The duration parameter is ignored.  */
    public static Toast makeText(Context ctx, CharSequence text, int duration) {
        createWrappedToast(ctx);
        return new Toast(text.toString());
    }
    
    /** Causes all toast messages to disappear. */
    public static synchronized void cancelAllToasts() {
        for (Toast t : new HashSet<Toast>(allToasts)) {
            t.cancel();
        }
    }

    /**
     * Synchronously waits until the toast display has been updated. Call this
     * from your tests, e.g., before verifying a TestingTransaction.
     */
    public static void waitForToastsToBeUpdated() {
        if (isOnUIThread()) {
            throw new IllegalStateException("Cannot wait for toasts from the UI thread");
        }
        
        // Toast view must be shown iff there are toasts. 
        while (!areToastsUpdated()) {
            try {
                Thread.sleep(POLLING_SLEEP_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException("Interrupted while waiting for toasts", e);
            }
        }
    }

    /** Computes whether toasts are updated */
    private static synchronized boolean areToastsUpdated() {
        if (wrappedToast == null) {
            return true;
        }
        // Toast view must be shown iff there are toasts.
        return wrappedToast.getView().isShown() == allToasts.isEmpty();
    }
    
    /** Show the toast message. */
    public void show() {
        Log.d("TOAST", "Showing " + this);
        synchronized (Toast.class) {
            allToasts.add(this);
            scheduledFuture = TIMER.schedule(new CancelToastRunnable(this),
                    TOAST_DELAY, TimeUnit.MILLISECONDS);
        }
        updateView();
    }
    
    /** Remove this toast from the screen. */
    public void cancel() {
        Log.d("TOAST", "Canceling " + this);
        synchronized (Toast.class) {
            allToasts.remove(this);
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
                scheduledFuture = null;
            }
        }
        updateView();
    }
    
    @Override
    public String toString() {
        return "Toast(\"" + text + "\")"; 
    }
    
    /**
     * This method must be called when a toast is changed; it updates the view
     * according to the current set of toasts.
     */
    private static synchronized void updateView() {
        if (!isOnUIThread()) {
            throw new IllegalStateException("Must show/cancel toasts on the UI thread");
        }
        
        if (allToasts.isEmpty() && wrappedToast != null) {
            wrappedToast.cancel();
            return;
        }
        
        StringBuilder allMessages = new StringBuilder();
        Iterator<Toast> i = allToasts.iterator();
        while (i.hasNext()) {
            allMessages.append(i.next().text);
            if (i.hasNext()) {
                allMessages.append("\n\n");
            }
        }
        wrappedToast.setText(allMessages);
        wrappedToast.show();
    }

    /** Creates the underlying wrapped toast, if needed */
    @SuppressLint("ShowToast")
    private static synchronized void createWrappedToast(Context ctx) {
        if (Toast.context != null && Toast.context != ctx) {
            // We changed activity. Cancel all toasts and reset
            cancelAllToasts();
            wrappedToast = null;
        }
        Toast.context = ctx;
        if (wrappedToast == null) {
            wrappedToast = android.widget.Toast.makeText(ctx, "", LENGTH_SHORT);
        }
    }
    /** Returns true if we're currently running on the UI thread */
    private static boolean isOnUIThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
