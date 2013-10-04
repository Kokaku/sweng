package epfl.sweng.servercomm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;

import android.os.AsyncTask;

/**
 * An AsyncTask that post the question given in parameter as String to server.
 * And return true if correctly sent.
 * @author ValentinRutz
 * 
 */
public class PostQuestionTask extends
        AsyncTask<String, Integer, Boolean> {

    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Boolean doInBackground(String... jsonString) {
        try {
        	HttpPost post = new HttpPost(ServerCommunication.SERVER_URL);
            post.setEntity(new StringEntity(jsonString[0]));
            post.setHeader("Content-type", "application/json");
            ResponseHandler<String> handler = new BasicResponseHandler();
            /* returns a string corresponding to the response HTTP */
            SwengHttpClientFactory.getInstance().execute(post, handler);
            return true;
        } catch (UnsupportedEncodingException e1) {
        } catch (IOException e) {
        }
        return false;
    }

}
