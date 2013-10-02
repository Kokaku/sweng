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

/**
 * @author kokaku
 *
 */
public class RandomQuestion extends ServerQuestion {
    
	/**
	 * Get a random question from server
	 * @throws ServerCommunicationException thrown if there is a problem with the server communication
	 */
	public RandomQuestion() throws ServerCommunicationException {
		super(fetchRandomQuestion(TOTAL_CONNECTION_TRY_BEFORE_ABORD));
	}
	
	private static JSONObject fetchRandomQuestion(int numberOfTryBeforeAbord) throws ServerCommunicationException {
		while (numberOfTryBeforeAbord > 0) {
			System.out.println("hello "+numberOfTryBeforeAbord);
			try {
				HttpGet request = new HttpGet(SERVER_URL + "random");
				ResponseHandler<String> handler = new BasicResponseHandler();
				String question = SwengHttpClientFactory.getInstance().execute(request, handler);
				JSONObject json = new JSONObject(question);
				return json;
			} catch (JSONException e) {
			} catch (ClientProtocolException e) {
			} catch (IOException e) {
			}
			numberOfTryBeforeAbord--;
		}

		throw new ServerCommunicationException("Couldn't fetch random question from server.");
	}
}
