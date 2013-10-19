package epfl.sweng.servercomm;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import epfl.sweng.authentication.AuthenticationState;
import epfl.sweng.authentication.UserCredentials;
import epfl.sweng.questions.QuizQuestion;
import epfl.sweng.utils.JSONUtilities;

/**
 * This class allows communication with the question server. Used to fetch
 * questions and send new ones to the server.
 * 
 * @author kokaku
 * 
 */
public final class ServerCommunication {

    private static final String SERVER_URL = "https://sweng-quiz.appspot.com/quizquestions/";
    private final static String SERVER_LOGIN_URL = "https://sweng-quiz.appspot.com/login";
    private final static String TEQUILA_URL = "https://tequila.epfl.ch/cgi-bin/tequila/login";

    private ServerCommunication() {
    }

    /**
     * Sends a question to the server
     * 
     * @param question
     *            to send to the server
     * @return true if the question has been correctly sent
     */
    public static boolean send(QuizQuestion question) {
        if (AuthenticationState.getState() != AuthenticationState.AUTHENTICATED) {
            return false;
        }
        
        if (question != null) {
            try {
                HttpPost request = new HttpPost(SERVER_URL);
                request.setEntity(new StringEntity(JSONUtilities
                        .getJSONString(question)));
                request.setHeader("Content-type", "application/json");
                // TODO waiting for UserCredentials completion to get sessionID
                request.setHeader("Authorization", "Tequila " + "sessionID");

                String httpAnswer = new HttpTask().execute(request).get();
                return httpAnswer != null && !httpAnswer.equals("error");
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            } catch (JSONException e) {
            } catch (UnsupportedEncodingException e) {
            }
        }

        return false;
    }

    /**
     * Fetch a random question from the server
     * 
     * @return the random question fetched, null if an error occurred
     */
    public static QuizQuestion getRandomQuestion() {
        if (AuthenticationState.getState() != AuthenticationState.AUTHENTICATED) {
            return null;
        }
        
        try {
            HttpUriRequest request = new HttpGet(SERVER_URL + "random");
            // TODO waiting for UserCredentials completion to get sessionID
            request.setHeader("Authorization", "Tequila " + "sessionID");

            String httpAnswer = new HttpTask().execute(request).get();
            if (httpAnswer != null && !httpAnswer.equals("error")) {
                JSONObject json = new JSONObject(httpAnswer);
                return new QuizQuestion(json.getString("question"),
                        JSONUtilities.parseAnswers(json),
                        json.getInt("solutionIndex"),
                        JSONUtilities.parseTags(json));
            }
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (JSONException e) {
        } catch (IllegalArgumentException e) {
        }

        return null;
    }

    /**
     * This method is called to authenticate user and get a session id
     * You must be in state "AuthenticationState.UNAUTHENTICATED" before calling
     * this method.
     * If this method return false you can be in any state of authentication
     * 
     * @param username a String representing the user to log
     * @param password aString representing the password of the user
     * @return true if correctly authenticated
     */
    public static boolean login(String username, String password) {
        if (AuthenticationState.getState() != AuthenticationState.UNAUTHENTICATED) {
            return false;
        }

        try {
            String httpAnswer = requestToken();
            AuthenticationState.setState(AuthenticationState.TOKEN);
            if (httpAnswer != null) {
                JSONObject json = new JSONObject(httpAnswer);
                String token = json.getString("token");
                
                //Don't know how to control if the request is a success or a fail
                httpAnswer = authTequila(token, username, password);
                AuthenticationState.setState(AuthenticationState.TEQUILA);

                httpAnswer = requestSessionID(token);
                AuthenticationState.setState(AuthenticationState.CONFIRMATION);
                
                if (httpAnswer != null) {
                    json = new JSONObject(httpAnswer);
                    String session = json.getString("session");
                    AuthenticationState
                            .setState(AuthenticationState.AUTHENTICATED);
                    UserCredentials.INSTANCE.saveUserCredentials(session);
                    return true;
                }
            }
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        } catch (JSONException e) {
        } catch (UnsupportedEncodingException e) {
        }

        return false;
    }

    private static String requestToken() throws
        InterruptedException, ExecutionException {
        
        HttpUriRequest request = new HttpGet(SERVER_LOGIN_URL);
        // Header is HTTP/1.1 200 ok
        return new HttpTask().execute(request).get();
    }

    private static String authTequila(
            String token,
            String username,
            String password) throws InterruptedException,
                                    ExecutionException,
                                    UnsupportedEncodingException {
        
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("requestkey", token));
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));

        HttpPost postRequest = new HttpPost(TEQUILA_URL);
        postRequest.setEntity(new UrlEncodedFormEntity(params));
        postRequest.setHeader("Content-type", "application/json");
        // Some errors possible ??
        // Normal connection return string "error" due to a ioException
        // But header is HTTP/1.1 302 Found but don't know how to read it
        return new HttpTask().execute(postRequest).get();
    }

    private static String requestSessionID(String token) throws
        UnsupportedEncodingException, InterruptedException, ExecutionException {
        
        HttpPost postRequest = new HttpPost(SERVER_LOGIN_URL);
        postRequest.setEntity(new StringEntity("{\"token\": \"" + token
                + "\"}"));
        postRequest.setHeader("Content-type", "application/json");
        return new HttpTask().execute(postRequest).get();
    }
}
