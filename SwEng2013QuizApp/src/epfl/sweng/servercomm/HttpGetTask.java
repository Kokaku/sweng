package epfl.sweng.servercomm;

import java.io.IOException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;

import android.os.AsyncTask;

/**
 * An AsyncTask that fetch a question from server at address given in parameter.
 * And return it in a JSON form. Return null if an error occurred.
 * 
 * @author kokaku
 * 
 */
public class HttpGetTask extends AsyncTask<String, Integer, String> {

    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected String doInBackground(String... serverURL) {
        try {
            HttpGet request = new HttpGet(serverURL[0]);
            ResponseHandler<String> handler = new BasicResponseHandler();
            String httpAnswer = SwengHttpClientFactory.getInstance().execute(
                    request, handler);
            return httpAnswer;
        } catch (IOException e) {
        }

        return null;
    }

}
