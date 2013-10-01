/**
 * 
 */
package epfl.sweng;

import java.util.Set;

/**
 * This class represents a question.
 * 
 * @author lseguy
 * 
 */

public class Question {
    public static final long DEFAULT_ID = 0;

    private long id;
    private String question;
    private String[] answers;
    private int solutionIndex;
    private Set<String> tags;

    /*
     * public Question(String question, String[] answers, int solutionIndex,
     * Set<String> tags) { id = DEFAULT_ID; this.question = question;
     * this.answers = answers; this.solutionIndex = solutionIndex; this.tags =
     * tags; }
     */

    /**
     * Default constructor. Create a random question.
     */
    public Question() {

    }

    public String getQuestion() {
        return question;
    }
    /*
     * private JSONObject getRandomFromServer() { HttpGet randomGet = new
     * HttpGet("https://sweng-quiz.appspot.com/quizquestions/random");
     * ResponseHandler<String> randomHandler = new BasicResponseHandler(); try {
     * String question = SwengHttpClientFactory.getInstance().execute(randomGet,
     * randomHandler); } catch (ClientProtocolException e) { // TODO
     * Auto-generated catch block e.printStackTrace(); } catch (IOException e) {
     * // TODO Auto-generated catch block e.printStackTrace(); } }
     */
}
