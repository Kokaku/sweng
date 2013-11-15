package epfl.sweng.servercomm;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import epfl.sweng.SwEng2013QuizApp;
import epfl.sweng.authentication.UserCredentials;
import epfl.sweng.authentication.UserCredentials.AuthenticationState;
import epfl.sweng.exceptions.InvalidCredentialsException;
import epfl.sweng.exceptions.NotLoggedInException;
import epfl.sweng.exceptions.ServerCommunicationException;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.utils.JSONUtilities;

/**
 * This singleton class allows communication with servers such as the question
 * server and the Tequila server.
 * 
 * It is used to get questions and send new ones to the server.
 * 
 * @author kokaku
 * 
 */
public enum ServerCommunication implements QuestionsCommunicator {
	INSTANCE;

	private static final String SERVER_URL = "https://sweng-quiz.appspot.com/quizquestions";
	private static final String SERVER_LOGIN_URL = "https://sweng-quiz.appspot.com/login";
	private static final String TEQUILA_URL = "https://tequila.epfl.ch/cgi-bin/tequila/login";

	private ServerCommunication() {
	}
	
	/**
	 * Sends a question to the server. This is a blocking method and thus it
	 * should be called by a class extending {@link AsyncTask}.
	 * 
	 * @param question the question to be sent
	 * @throws ServerCommunicationException if the network request is unsuccessful
	 * @return the question updated with id and owner fields assigned by the server
	 */
	@Override
	public QuizQuestion send(QuizQuestion question)
		throws ServerCommunicationException {

		if (!isNetworkAvailable()) {
			throw new ServerCommunicationException("Not connected.");
		}

		HttpPost request = new HttpPost(SERVER_URL);
		request.setHeader("Content-type", "application/json");
		addAuthenticationHeader(request);
		
		QuizQuestion updatedQuestion = null;
		
		try {
    		request.setEntity(new StringEntity(JSONUtilities.getJSONString(question)));
    		String responseBody = sendHttpRequest(request, HttpStatus.SC_CREATED,
    		    "Unable to send a question.");
    		updatedQuestion = new QuizQuestion(responseBody);
		} catch (JSONException e) {
		    throw new ServerCommunicationException("JSON badly formatted.");
		} catch (UnsupportedEncodingException e) {
		    throw new ServerCommunicationException("Encoding exception.");
        }

		return updatedQuestion;
	}

	/**
	 * Fetches a random question from the server. This is a blocking method and
	 * thus it should be called by a class extending {@link AsyncTask}.
	 * 
	 * @return a question fetched from the server
	 * @throws ServerCommunicationException if the network request is unsuccessful
	 */
	@Override
	public QuizQuestion getRandomQuestion()
		throws ServerCommunicationException, NotLoggedInException {

		if (!isNetworkAvailable()) {
			throw new ServerCommunicationException("Not connected.");
		}

		HttpGet request = new HttpGet(SERVER_URL + "/random");
		addAuthenticationHeader(request);

		QuizQuestion question = null;
		
		try {
		    String responseBody = sendHttpRequest(request, HttpStatus.SC_OK,
		        "Unable to get a question.");
			question = new QuizQuestion(responseBody);
		} catch (JSONException e) {
            throw new ServerCommunicationException("JSON badly formatted.");
        }
		
		return question;
	}

	/**
	 * Authenticate the user and get a session id. You must be in state @{code
	 * UNAUTHENTICATED} before calling this method. This is a blocking method
	 * and thus it should be called by a class extending {@link AsyncTask}.
	 * 
	 * @param username a String representing the user's name
	 * @param password a String representing the user's password
	 * @throws InvalidCredentialsException if the username or password is incorrect
	 * @throws ServerCommunicationException if unable to log in
	 */
	public void login(String username, String password)
	    throws ServerCommunicationException, InvalidCredentialsException {

		if (!isNetworkAvailable()) {
			throw new ServerCommunicationException("Not connected");
		} else if (UserCredentials.INSTANCE.getState() == AuthenticationState.AUTHENTICATED) {
		    return; // already logged in or login in
		}

		try {
			UserCredentials.INSTANCE.setState(AuthenticationState.TOKEN);
			String httpResponse = requestToken();
			JSONObject json = new JSONObject(httpResponse);
			String token = json.getString("token");

			UserCredentials.INSTANCE.setState(AuthenticationState.TEQUILA);
			authTequila(token, username, password);

			UserCredentials.INSTANCE.setState(AuthenticationState.CONFIRMATION);
			httpResponse = requestSessionID(token);

			json = new JSONObject(httpResponse);
			String session = json.getString("session");

			UserCredentials.INSTANCE.setState(AuthenticationState.AUTHENTICATED);
			UserCredentials.INSTANCE.saveUserCredentials(session);
		} catch (JSONException e) {
			throw new ServerCommunicationException("JSON badly formatted.");
		}
	}
	
	/**
	 * Send a request.
	 * 
	 * @param request the request to be sent
	 * @param expectedStatus the expected status
	 * @param errorMessage the message if an exception is thrown
	 * @return the response body
	 * @throws ServerCommunicationException if the status is not the one which is expected
	 */
	private String sendHttpRequest(HttpRequestBase request, int expectedStatus,
        String errorMessage)
        throws ServerCommunicationException {
        
        ResponseHandler<String> handler = new BasicResponseHandler();
        HttpResponse httpResponse = null;
        
        try {
            httpResponse = SwengHttpClientFactory.getInstance().execute(request);
            
            if (httpResponse != null && httpResponse.getStatusLine().getStatusCode() == expectedStatus) {
                return handler.handleResponse(httpResponse);
            }
        } catch (IOException e) {
        }
        
        throw new ServerCommunicationException(errorMessage);
    }

	/**
	 * @return true if the device is connected, false otherwise.
	 */
	private boolean isNetworkAvailable() {
		Context context = SwEng2013QuizApp.getAppContext();
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		return networkInfo != null && networkInfo.isConnected();
	}

	private void addAuthenticationHeader(HttpUriRequest request) {
		request.setHeader("Authorization", "Tequila "
				+ UserCredentials.INSTANCE.getSessionID());
		Log.v("POTATO ServerCom - addAuthenticationHeader",
				"Adding athentication header");

	}

	private String requestToken()
	    throws ServerCommunicationException {

		HttpGet request = new HttpGet(SERVER_LOGIN_URL);
		return sendHttpRequest(request, HttpStatus.SC_OK,
		    "Unable to get a token from server.");
	}

	private void authTequila(String token, String username, String password)
	    throws ServerCommunicationException, InvalidCredentialsException {

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("requestkey", token));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));

		HttpPost request = new HttpPost(TEQUILA_URL);

		try {
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ServerCommunicationException("Encoding exception.");
        }
		
		sendHttpRequest(request, HttpStatus.SC_MOVED_TEMPORARILY,
		    "Unable to confirm token with Tequila.");
	}

	private String requestSessionID(String token)
	    throws ServerCommunicationException {

		HttpPost request = new HttpPost(SERVER_LOGIN_URL);
		request.setHeader("Content-type", "application/json");

		String sessionId = null;

		try {
			request.setEntity(new StringEntity("{\"token\": \"" + token + "\"}"));
			sessionId = sendHttpRequest(request, HttpStatus.SC_OK,
			    "Unable to get a valid session ID.");
		} catch (UnsupportedEncodingException e) {
            throw new ServerCommunicationException("Encoding exception.");
        }

		return sessionId;
	}
}
