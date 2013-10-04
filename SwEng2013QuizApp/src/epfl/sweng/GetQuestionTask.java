package epfl.sweng;

import java.io.IOException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import epfl.sweng.servercomm.SwengHttpClientFactory;

/**
 * An AsyncTask that fetch a question from server at address given in parameter.
 * And return it in a JSON form. Return null if an error occurred.
 * @author kokaku
 * 
 */
public class GetQuestionTask extends AsyncTask<String, Integer, JSONObject> {

    protected static final int TOTAL_CONNECTION_TRY_BEFORE_ABORT = 5;

    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected JSONObject doInBackground(String... serverURL) {
        for (int i = 0; i < TOTAL_CONNECTION_TRY_BEFORE_ABORT; i++) {
            try {
                HttpGet request = new HttpGet(serverURL[0]);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String question = SwengHttpClientFactory.getInstance().execute(
                        request, handler);
                JSONObject json = new JSONObject(question);
                return json;
            } catch (JSONException e) {
            } catch (IOException e) {
            }
        }

        return null;
    }

}
