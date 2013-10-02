/**
 * 
 */
package epfl.sweng;

/**
 * @author kokaku
 *
 */
public class QuestionGetter extends ServerQuestion {

    public QuestionGetter(String urlTermination) throws ServerCommunicationException {
        super(new GetQuestionTask().doInBackground(SERVER_URL+urlTermination));
    }
}
