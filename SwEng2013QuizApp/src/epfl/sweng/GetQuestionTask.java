/**
 * 
 */
package epfl.sweng;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.servercomm.SwengHttpClientFactory;

import android.os.AsyncTask;

/**
 * @author kokaku
 *
 */
public class GetQuestionTask extends AsyncTask<String, Integer, JSONObject> {

    protected static final int TOTAL_CONNECTION_TRY_BEFORE_ABORT = 5;
    
    /* (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected JSONObject doInBackground(String... serverURL) {
        for (int i = 0; i < TOTAL_CONNECTION_TRY_BEFORE_ABORT; i++) {
            try {
                HttpGet request = new HttpGet(serverURL[0]);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String question = SwengHttpClientFactory.getInstance().execute(request, handler);
                JSONObject json = new JSONObject(question);
                return json;
            } catch (JSONException e) {
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }
        }
        
      //TODO
        /*
         * Do something to tell UI that they will never get their question !!!
         */
        throw new RuntimeException("Couldn't fetch random question from server.");
    }

}
