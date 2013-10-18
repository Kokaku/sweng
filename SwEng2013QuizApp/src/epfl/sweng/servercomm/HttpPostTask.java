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
public class HttpPostTask extends
        AsyncTask<String, Integer, String> {

    private enum HttpParams {
        URL, REQUEST, HEADER_NAME, HEADER_CONTENT;
    }
    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected String doInBackground(String... postParams) {
        try {
        	HttpPost post = new HttpPost(postParams[HttpParams.URL.ordinal()]);
            post.setEntity(new StringEntity(
                    postParams[HttpParams.REQUEST.ordinal()]));
            post.setHeader(
                    postParams[HttpParams.HEADER_NAME.ordinal()],
                    postParams[HttpParams.HEADER_CONTENT.ordinal()]);
            ResponseHandler<String> handler = new BasicResponseHandler();
            /* returns a string corresponding to the response HTTP */
            return SwengHttpClientFactory.getInstance().execute(post, handler);
        } catch (UnsupportedEncodingException e1) {
        } catch (IOException e) {
        }
        return "error";
    }

}
