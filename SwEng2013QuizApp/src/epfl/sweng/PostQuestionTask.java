/**
 * 
 */
package epfl.sweng;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;

import android.os.AsyncTask;
import epfl.sweng.servercomm.SwengHttpClientFactory;

/**
 * @author ValentinRutz
 *
 */
public class PostQuestionTask extends AsyncTask<ServerQuestion, Integer, Boolean> {

    /* (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Boolean doInBackground(ServerQuestion... question) {
        HttpPost post = new HttpPost(ServerQuestion.SERVER_URL);
        try {
            post.setEntity(new StringEntity("{" +
                " \"question\": \"What is the answer to life, the universe and everything?\"," +
                " \"answers\": [ \"42\", \"24\" ]," +
                " \"solutionIndex\": 0," +
                " \"tags\": [ \"h2g2\", \"trivia\" ]" +
                " }"));
            post.setHeader("Content-type", "application/json");
            ResponseHandler<String> handler = new BasicResponseHandler();
            /* returns a string corresponding to the response HTTP*/
            String response = SwengHttpClientFactory.getInstance().execute(post, handler);
            return true;
        } catch (UnsupportedEncodingException e1) {
        } catch (IOException e) {
        }
        return false;
    }

}
