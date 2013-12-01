package epfl.sweng.servercomm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import epfl.sweng.SwEng2013QuizApp;
import epfl.sweng.authentication.UserCredentials;
import epfl.sweng.authentication.UserCredentials.AuthenticationState;
import epfl.sweng.exceptions.BadRequestException;
import epfl.sweng.exceptions.DBException;
import epfl.sweng.exceptions.InvalidCredentialsException;
import epfl.sweng.exceptions.NotLoggedInException;
import epfl.sweng.exceptions.ServerCommunicationException;
import epfl.sweng.quizquestions.QuizQuestion;
import epfl.sweng.searchquestions.QuestionIterator;
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

	private static final String SERVER_URL = "https://sweng-quiz.appspot.com";
	private static final String SERVER_LOGIN_URL = "https://sweng-quiz.appspot.com/login";
	private static final String TEQUILA_URL = "https://tequila.epfl.ch/cgi-bin/tequila/login";
	
	private ServerCommunication() {
	    //Log.d("POTATO SeverCom", "Constructor called");
	}
	
	/**
	 * Sends a question to the server. This is a blocking method and thus it
	 * should be called by a class extending {@link AsyncTask}.
	 * 
	 * @param question
	 *            the question to be sent
	 * @throws ServerCommunicationException
	 *             if the network request is unsuccessful
	 * @return the question updated with id and owner fields assigned by the
	 *         server or null if the error code is 3xx or 4xx
	 */
	@Override
	public QuizQuestion send(QuizQuestion question)
		throws ServerCommunicationException {
		
	    int responseStatus = 0;
	    String httpBody = null;
	    
		//Log.d("POTATO ServerCom", "Starting to send question = " + question);
		if (!isNetworkAvailable()) {
			//Log.d("POTATO ServerCom", "Network is not available to send question");
			throw new ServerCommunicationException("Not connected.");
		}

		HttpPost request = new HttpPost(SERVER_URL + "/quizquestions");

		request.setHeader("Content-type", "application/json");
		addAuthenticationHeader(request);

		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpResponse httpResponse = null;
		QuizQuestion updatedQuestion = null;

		try {
			request.setEntity(new StringEntity(JSONUtilities
					.getJSONString(question)));

			httpResponse = SwengHttpClientFactory.getInstance().execute(request);
	        responseStatus = httpResponse.getStatusLine().getStatusCode();
		    httpBody = handler.handleResponse(httpResponse);
			//Log.d("POTATO ServerCom", "httpBody = " + httpBody + " status = " + responseStatus);
			updatedQuestion = new QuizQuestion(httpBody);
			//Log.d("POTATO ServerCom", "updated question = " + updatedQuestion);
		} catch (IOException e) {
			//Log.d("POTATO ServerCom", "IO Exception");
			// Status code is 3xx or 4xx
			if (responseStatus >= HttpStatus.SC_MULTIPLE_CHOICES
			    && responseStatus < HttpStatus.SC_INTERNAL_SERVER_ERROR) {
			    throw new BadRequestException("Status code is " + responseStatus);
			} else {
			    throw new ServerCommunicationException("Unable to send the question to the server. " +
			        "Status code is " + responseStatus);
			}
		} catch (JSONException e) {
			//Log.d("POTATO ServerCom", "JSON exception");
			throw new ServerCommunicationException("JSON badly formatted. "
					+ e.getMessage());
		}
		
		if (httpBody == null || responseStatus != HttpStatus.SC_CREATED) {
		    //Log.d("POTATO ServerCom", "httpBody is " + httpBody + " and status is " + responseStatus);
		    throw new ServerCommunicationException("Unable to send the question to the server.");
		}
		
		//Log.d("POTATO ServerCom", "question sent, updated question = " + updatedQuestion);
		return updatedQuestion;
	}

	/**
	 * Fetches a random question from the server. This is a blocking method and
	 * thus it should be called by a class extending {@link AsyncTask}.
	 * 
	 * @return a question fetched from the server
	 * @throws ServerCommunicationException
	 *             if the network request is unsuccessful
	 */
	@Override
	public QuizQuestion getRandomQuestion()
		throws ServerCommunicationException, NotLoggedInException {
	    
	    int responseStatus = 0;
        String httpBody = null;
	    
		//Log.d("POTATO ServerCom", "Starting to fetch a question");
		if (!isNetworkAvailable()) {
			//Log.d("POTATO ServerCom", "Network not available in getRQuestion");
			throw new ServerCommunicationException("Not connected.");
		}

		HttpUriRequest request = new HttpGet(SERVER_URL + "/quizquestions/random");
		addAuthenticationHeader(request);

		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpResponse httpResponse = null;
		try {
			httpResponse = SwengHttpClientFactory.getInstance().execute(request);
			if (httpResponse == null) {
				//Log.d("POTATO ServerCom", "Response null, ServerComException thrown");
			    throw new ServerCommunicationException("Unable to contact server");
			}
			responseStatus = httpResponse.getStatusLine().getStatusCode();
			httpBody = handler.handleResponse(httpResponse);
			//Log.d("POTATO ServerCom", "Response: body = " + httpBody + " status = " + responseStatus);
		} catch (IOException e) {
		    //Log.d("POTATO ServerCom", "IO exception, nothing is done :)");
            // Status code is 3xx or 4xx
            if (responseStatus >= HttpStatus.SC_MULTIPLE_CHOICES
                && responseStatus < HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                throw new BadRequestException("Status code is " + responseStatus);
            } else {
                throw new ServerCommunicationException("Unable to send the question to the server. " +
                    "Status code is " + responseStatus);
            }
		}
		

		if (httpBody == null || responseStatus != HttpStatus.SC_OK) {
			//Log.d("POTATO ServerCom", "httpBody is null, or status not correct");
			throw new ServerCommunicationException("Unable to get a question from the server.");
		}

		try {
			QuizQuestion question = new QuizQuestion(httpBody);
			//Log.d("POTATO ServerCom", "Question fetched: " + question);
			return question;
		} catch (JSONException e) {
			//Log.d("POTATO ServerCom", "JSON badly formatted.");
			throw new ServerCommunicationException("JSON badly formatted.");
		}
	}

	/**
	 * Authenticate the user and get a session id. You must be in state @{code
	 * UNAUTHENTICATED} before calling this method. This is a blocking method
	 * and thus it should be called by a class extending {@link AsyncTask}.
	 * 
	 * @param username
	 *            a String representing the user's name
	 * @param password
	 *            a String representing the user's password
	 * @throws InvalidCredentialsException
	 *             if the username or password is incorrect
	 * @throws ServerCommunicationException
	 *             if unable to log in
	 */
	public void login(String username, String password)
	    throws ServerCommunicationException, InvalidCredentialsException {

		if (!isNetworkAvailable()) {
			//Log.d("POTATO ServerCom - login", "Network not available in login");

			throw new ServerCommunicationException("Not connected");
		}/*
		 * else if (UserCredentials.INSTANCE.getState() ==
		 * AuthenticationState.AUTHENTICATED) { return; // already logged in or
		 * login in }
		 */

		try {
			//Log.d("POTATO ServerCom - login", "Start loging in");

			UserCredentials.INSTANCE.setState(AuthenticationState.TOKEN);
			Log.v("POTATO ServerCom - login", "State: TOKEN "
					+ UserCredentials.INSTANCE.getState());
			
			String httpResponse = requestToken();
			JSONObject json = new JSONObject(httpResponse);
			String token = json.getString("token");
			Log.v("POTATO ServerCom - login", "token = " + token);

			UserCredentials.INSTANCE.setState(AuthenticationState.TEQUILA);
			Log.v("POTATO ServerCom - login", "State: TEQUILA "
					+ UserCredentials.INSTANCE.getState());

			authTequila(token, username, password);

			UserCredentials.INSTANCE.setState(AuthenticationState.CONFIRMATION);
			Log.v("POTATO ServerCom - login", "State: CONFIRMATION "
					+ UserCredentials.INSTANCE.getState());

			httpResponse = requestSessionID(token);

			json = new JSONObject(httpResponse);
			String session = json.getString("session");
			Log.v("POTATO ServerCom - login", "session = " + session);

			UserCredentials.INSTANCE
					.setState(AuthenticationState.AUTHENTICATED);
			UserCredentials.INSTANCE.saveUserCredentials(session);
		} catch (JSONException e) {
			UserCredentials.INSTANCE
					.setState(AuthenticationState.UNAUTHENTICATED);
			Log.v("POTATO ServerCom - login", "Exception: JSON");
			throw new ServerCommunicationException("JSON badly formatted.");
		} catch (ServerCommunicationException e) {
			Log.v("POTATO ServerCom - login", "Exception: ServerCom");
			UserCredentials.INSTANCE
					.setState(AuthenticationState.UNAUTHENTICATED);
			throw e;
		} catch (InvalidCredentialsException e) {
			Log.v("POTATO ServerCom - login", "Exception: InvalidCred");
			UserCredentials.INSTANCE
					.setState(AuthenticationState.UNAUTHENTICATED);
			throw e;
		}
	}

	/**
	 * @return true if the device is connected, false otherwise.
	 */
	public boolean isNetworkAvailable() {
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

	private String requestToken() throws ServerCommunicationException {

	    int responseStatus = 0;
	    String httpBody = null;
	    
		HttpUriRequest request = new HttpGet(SERVER_LOGIN_URL);

		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpResponse httpResponse = null;
		try {
			httpResponse = SwengHttpClientFactory.getInstance().execute(request);
            if (httpResponse == null) {
                throw new ServerCommunicationException("Unable to contact server");
            }
			httpBody = handler.handleResponse(httpResponse);
			responseStatus = httpResponse.getStatusLine().getStatusCode();
		} catch (IOException e) {
		}

		//Log.d("POTATO ServerCom", "status = " + responseStatus);
		
		if (httpBody == null || responseStatus != HttpStatus.SC_OK) {
			Log.v("POTATO ServerCom - requestToken", "Exception: request = "
					+ httpResponse + " status = " + responseStatus);

			throw new ServerCommunicationException("Unable to get a token.");
		}
		Log.v("POTATO ServerCom - requestToken", "httpResponse = "
				+ httpResponse);

		return httpBody;
	}

	private void authTequila(String token, String username, String password)
	    throws ServerCommunicationException, InvalidCredentialsException {

        int responseStatus = 0;
	    
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("requestkey", token));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));

		HttpPost request = new HttpPost(TEQUILA_URL);

        HttpResponse httpResponse = null;
        
		try {
			request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			httpResponse = SwengHttpClientFactory.getInstance().execute(request);
			if (httpResponse == null) {
                throw new ServerCommunicationException("Unable to contact server");
            }
			responseStatus = httpResponse.getStatusLine().getStatusCode();
		} catch (IOException e) {
        	Log.v("POTATO ServerCom - authTequila", "Exception: IO");
		}

    	Log.v("POTATO ServerCom - authTequila", "Response status = " + responseStatus);

		if (responseStatus != HttpStatus.SC_MOVED_TEMPORARILY) {
			throw new InvalidCredentialsException(
					"Unable to authenticate with Tequila.");
			
		}

	}

	private String requestSessionID(String token)
	    throws ServerCommunicationException {

        int responseStatus = 0;
        String httpBody = null; 
	    
		HttpPost request = new HttpPost(SERVER_LOGIN_URL);
		request.setHeader("Content-type", "application/json");

		ResponseHandler<String> handler = new BasicResponseHandler();
		HttpResponse httpResponse = null;

		try {
			request.setEntity(new StringEntity("{\"token\": \"" + token + "\"}"));
			httpResponse = SwengHttpClientFactory.getInstance().execute(
					request);
            if (httpResponse == null) {
                throw new ServerCommunicationException("Unable to contact server");
            }
			responseStatus = httpResponse.getStatusLine().getStatusCode();
			httpBody = handler.handleResponse(httpResponse);
		} catch (IOException e) {
		}
		
    	Log.v("POTATO ServerCom - requestSessionID", "Response status = " 
    			+ responseStatus + "response = " + httpResponse);

		if (httpBody == null || responseStatus != HttpStatus.SC_OK) {
			throw new ServerCommunicationException("Unable to confirm token.");
		}

		return httpBody;
	}

	/**
	 * This method sends a query to the server in SwEngQL and returns
	 * a {@link QuestionIterator} over these questions
	 * 
	 * @param query: query in the proposed language (SwEngQL)
     * @param next: hash pointing to the next page of searched questions
     * @return a {@link QuestionIterator} with the questions retrieved from
     *         the server
     * @throws NotLoggedInException if the user is not logged in
     * @throws DBException if the database couldn't retrieve a question
     * @throws ServerCommunicationException if the device is not connected or if
     *         the device cannot send the request to the server
     * @throws JSONException if the JSONObject was badly formatted
	 */
    @Override
    public QuestionIterator searchQuestion(String query, String next)
        throws NotLoggedInException, DBException, ServerCommunicationException {

        int responseStatus = 0;
        String httpBody = null;
        
        //Log.d("POTATO ServerCom", "Start sending query to the server = " + query);
        
        if (!isNetworkAvailable()) {
            //Log.d("POTATO ServerCom", "Network is not available to send query");
            throw new ServerCommunicationException("Not connected.");
        }
        
        HttpPost request = new HttpPost(SERVER_URL + "/search");
        
        request.setHeader("Content-type", "application/json");
        addAuthenticationHeader(request);
        
        ResponseHandler<String> handler = new BasicResponseHandler();
        HttpResponse httpResponse = null;
        QuestionIterator iterator = null;
        
        try {
            request.setEntity(new StringEntity(JSONUtilities
                    .getJSONQueryString(query, next)));
            
            httpResponse = SwengHttpClientFactory.getInstance().execute(request);
            responseStatus = httpResponse.getStatusLine().getStatusCode();
            httpBody = handler.handleResponse(httpResponse);

            //Log.d("POTATO ServerCom", "httpBody = " + httpBody + " status = " + responseStatus);
            
            iterator = httpResponseToQuestionIterator(query, httpBody);
        } catch (IOException e) {
            //Log.d("POTATO ServerCom", "IO Exception");
            if (responseStatus >= HttpStatus.SC_MULTIPLE_CHOICES
                && responseStatus < HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                // Status code is 3xx or 4xx
                throw new BadRequestException("Status code is " + responseStatus);
            } else {
                throw new ServerCommunicationException("Unable to send the question to the server. " +
                    "Status code is " + responseStatus);
            }
        } catch (JSONException e) {
            //Log.d("POTATO ServerCom", "JSON exception");
            throw new ServerCommunicationException("JSON badly formatted. "
                    + e.getMessage());
        }
        
        return iterator;
        
    }
    
    private QuestionIterator httpResponseToQuestionIterator(String query,
                                                            String httpBody)
        throws JSONException {

        JSONObject jsonQuestions = new JSONObject(httpBody);
        
        JSONArray jsonArrayQuestions = jsonQuestions.getJSONArray("questions");
        
        String jsonNext = null;
        if (!jsonQuestions.isNull("next")) {
            jsonNext = jsonQuestions.getString("next");
        }
        
        List<String> stringQuestions =
                JSONUtilities.parseJSONArrayToList(jsonArrayQuestions);
        QuizQuestion[] questions = new QuizQuestion[stringQuestions.size()];
        
        int counter = 0;
        for (String q : stringQuestions) {
            questions[counter] = new QuizQuestion(q);
            counter++;
        }
        
        return new QuestionIterator(questions, query, jsonNext);
    }
	
}
