/**
 * 
 */
package epfl.sweng;

import java.util.Set;

/**
 * @author kokaku
 *
 */
public class PostQuestion extends ServerQuestion {

	
	public PostQuestion(String question, String[] answers, int solutionIndex, Set<String> tags) {
		super(DEFAULT_ID, question, answers, solutionIndex, tags);
	}
	
	/**
	 * Not yet implemented
	 */
	public void postOnServer() {
		//TODO
//		HttpPost post = new HttpPost(SERVER_URL + "quizquestions");
//		post.setEntity(new StringEntity("{" +
//		    " \"question\": \"What is the answer to life, the universe and everything?\"," +
//		    " \"answers\": [ \"42\", \"24\" ]," +
//		    " \"solutionIndex\": 0," +
//		    " \"tags\": [ \"h2g2\", \"trivia\" ]" +
//		    " }"));
//		post.setHeader("Content-type", "application/json");
//		ResponseHandler<String> handler = new BasicResponseHandler();
//		String response = SwengHttpClientFactory.getInstance().execute(post, handler);
	}
}
