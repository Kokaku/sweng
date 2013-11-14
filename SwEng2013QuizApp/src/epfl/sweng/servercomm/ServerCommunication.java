package epfl.sweng.servercomm;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
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
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    private int mResponseStatus;

    private ServerCommunication() {
        addStatusInterceptor();
    }

    /**
     * Add a request interceptor which is used to check if a request has been
     * successful or not.
     */
    public void addStatusInterceptor() {
        final HttpResponseInterceptor responseInterceptor = new HttpResponseInterceptor() {
            @Override
            public void process(HttpResponse response, HttpContext context) {
                mResponseStatus = response.getStatusLine().getStatusCode();
            }
        };
        
        SwengHttpClientFactory.getInstance().addResponseInterceptor(
                responseInterceptor);
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
        
        ResponseHandler<String> handler = new BasicResponseHandler();
        String httpResponse = null;
        QuizQuestion updatedQuestion = null;
        
        try {
            request.setEntity(new StringEntity(JSONUtilities
                .getJSONString(question)));
            
            httpResponse = SwengHttpClientFactory.getInstance().execute(
                    request, handler);
            updatedQuestion = new QuizQuestion(httpResponse);
        } catch (IOException e) {
        } catch (JSONException e) {
            throw new ServerCommunicationException("JSON badly formatted. " + e.getMessage());
        }
        
        if (httpResponse == null || mResponseStatus != HttpStatus.SC_CREATED) {
            throw new ServerCommunicationException("Unable to send the question to the server.");
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
        
        HttpUriRequest request = new HttpGet(SERVER_URL + "/random");
        addAuthenticationHeader(request);
        
        ResponseHandler<String> handler = new BasicResponseHandler();
        String httpResponse = null;
        try {
            httpResponse = SwengHttpClientFactory.getInstance().execute(
                    request, handler);
        } catch (IOException e) {
        }
        
        if (httpResponse == null || mResponseStatus != HttpStatus.SC_OK) {
            throw new ServerCommunicationException("Unable to get a question from the server.");
        }
        
        try {
            return new QuizQuestion(httpResponse);
        } catch (JSONException e) {
            throw new ServerCommunicationException("JSON badly formatted.");
        }
    }
    
    /**
     * Authenticate the user and get a session id.
     * You must be in state @{code UNAUTHENTICATED} before calling this method.
     * This is a blocking method and thus it should be called by a class
     * extending {@link AsyncTask}.
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
        }/* else if (UserCredentials.INSTANCE.getState() == AuthenticationState.AUTHENTICATED) {
            return; // already logged in or login in
        }*/
        
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
            UserCredentials.INSTANCE.setState(AuthenticationState.UNAUTHENTICATED);
            throw new ServerCommunicationException("JSON badly formatted.");
        } catch (ServerCommunicationException e) {
            UserCredentials.INSTANCE.setState(AuthenticationState.UNAUTHENTICATED);
            throw e;
        } catch (InvalidCredentialsException e) {
            UserCredentials.INSTANCE.setState(AuthenticationState.UNAUTHENTICATED);
            throw e;
        }
    }
    
    /**
     * @return true if the device is connected, false otherwise.
     */
    private boolean isNetworkAvailable() {
        Context context = SwEng2013QuizApp.getAppContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) 
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        
        return networkInfo != null && networkInfo.isConnected();
    }
    
    private void addAuthenticationHeader(HttpUriRequest request) {
        request.setHeader("Authorization", "Tequila "
            + UserCredentials.INSTANCE.getSessionID());
    }
    
    private String requestToken()
        throws ServerCommunicationException {

        HttpUriRequest request = new HttpGet(SERVER_LOGIN_URL);
        
        ResponseHandler<String> handler = new BasicResponseHandler();
        String httpResponse = null;
        try {
            httpResponse = SwengHttpClientFactory.getInstance().execute(
                    request, handler);
        } catch (IOException e) {
        }
        
        if (httpResponse == null || mResponseStatus != HttpStatus.SC_OK) {
            throw new ServerCommunicationException("Unable to get a token.");
        }
        
        return httpResponse;
    }

    private void authTequila(String token, String username, String password)
        throws ServerCommunicationException, InvalidCredentialsException {

        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("requestkey", token));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        
        HttpPost request = new HttpPost(TEQUILA_URL);
        
        ResponseHandler<String> handler = new BasicResponseHandler();
        try {
            request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            SwengHttpClientFactory.getInstance().execute(
                    request, handler);
        } catch (IOException e) {
        }
        
        if (mResponseStatus != HttpStatus.SC_MOVED_TEMPORARILY) {
            throw new InvalidCredentialsException("Unable to authenticate with Tequila.");
        }
    }

    private String requestSessionID(String token)
        throws ServerCommunicationException {

        HttpPost request = new HttpPost(SERVER_LOGIN_URL);
        request.setHeader("Content-type", "application/json");
        
        ResponseHandler<String> handler = new BasicResponseHandler();
        String httpResponse = null;
        
        try {
            request.setEntity(new StringEntity("{\"token\": \"" + token + "\"}"));
            httpResponse = SwengHttpClientFactory.getInstance().execute(
                request, handler);
        } catch (IOException e) {
        }
        
        if (httpResponse == null || mResponseStatus != HttpStatus.SC_OK) {
            throw new ServerCommunicationException("Unable to confirm token.");
        }
        
        return httpResponse;
    }
}
