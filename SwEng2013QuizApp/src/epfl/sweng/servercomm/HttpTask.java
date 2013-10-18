package epfl.sweng.servercomm;

import java.io.IOException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;

import android.os.AsyncTask;

/**
 * An AsyncTask that fetch a question from server at address given in parameter.
 * And return it in a JSON form. Return null if an error occurred.
 * 
 * @author kokaku
 * 
 */
public class HttpTask extends AsyncTask<HttpUriRequest, Integer, String> {

    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected String doInBackground(HttpUriRequest... request) {
        try {
            ResponseHandler<String> handler = new BasicResponseHandler();
            String httpAnswer = SwengHttpClientFactory.getInstance().execute(
                    request[0], handler);
            return httpAnswer;
        } catch (IOException e) {
        }

        return "error";
    }

}
