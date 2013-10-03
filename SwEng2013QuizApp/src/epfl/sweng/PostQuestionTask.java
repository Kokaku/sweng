/**
 * 
 */
package epfl.sweng;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

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
public class PostQuestionTask extends
        AsyncTask<QuestionSender, Integer, Boolean> {

    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Boolean doInBackground(QuestionSender... questions) {
        HttpPost post = new HttpPost(ServerQuestion.SERVER_URL);
        QuestionSender question = questions[0];
        try {
            post.setEntity(new StringEntity("{"
                    + " \"question\": \""
                    + question.getQuestion()
                    + "\","
                    + " \"answers\": "
                    + question.convertIterableToJSONString(Arrays
                            .asList(question.getAnswers()))
                    + ","
                    + " \"solutionIndex\": "
                    + question.getSolutionIndex() + ","
                    + " \"tags\": "
                    + question.convertIterableToJSONString(question.getTags())
                    + " }"));
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
