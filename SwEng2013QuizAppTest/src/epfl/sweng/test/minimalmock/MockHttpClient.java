package epfl.sweng.test.minimalmock;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.AuthenticationHandler;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.RequestDirector;
import org.apache.http.client.UserTokenHandler;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;

import android.util.Log;


/** The SwEng HTTP Client */
public class MockHttpClient extends DefaultHttpClient {

    /** Prepared response */
    private class CannedResponse {
        private final Pattern pattern;
        private final int statusCode;
        private final String responseBody;
        private final String contentType;

        public CannedResponse(Pattern pattern, int statusCode, String responseBody, String contentType) {
            this.pattern = pattern;
            this.statusCode = statusCode;
            this.responseBody = responseBody;
            this.contentType = contentType;
        }
    }

    private final List<CannedResponse> responses = new ArrayList<CannedResponse>();

    public void pushCannedResponse(String requestRegex, int status, String responseBody, String contentType) {
        responses.add(0, new CannedResponse(Pattern.compile(requestRegex), status, responseBody, contentType));
    }

    public void popCannedResponse() {
        if (responses.isEmpty()) {
            throw new IllegalStateException("Canned response stack is empty!");
        }
        responses.remove(0);
    }

    @Override
    protected RequestDirector createClientRequestDirector(
            final HttpRequestExecutor requestExec,
            final ClientConnectionManager conman,
            final ConnectionReuseStrategy reustrat,
            final ConnectionKeepAliveStrategy kastrat,
            final HttpRoutePlanner rouplan,
            final HttpProcessor httpProcessor,
            final HttpRequestRetryHandler retryHandler,
            final RedirectHandler redirectHandler,
            final AuthenticationHandler targetAuthHandler,
            final AuthenticationHandler proxyAuthHandler,
            final UserTokenHandler stateHandler,
            final HttpParams params) {
        return new MockRequestDirector(this);
    }

    public HttpResponse processRequest(HttpRequest request) {
        for (CannedResponse cr : responses) {
            if (cr.pattern.matcher(request.getRequestLine().toString()).find()) {
                Log.v("HTTP", "Mocking request since it matches pattern " + cr.pattern);
                Log.v("HTTP", "Response body: " + cr.responseBody);
                return new MockHttpResponse(cr.statusCode, cr.responseBody, cr.contentType);
            }
        }

        return null;
    }
}

/**
 * A request director which does nothing else than passing the request back to
 * the MockHttpClient.
 */
class MockRequestDirector implements RequestDirector {

    private MockHttpClient httpClient;

    public MockRequestDirector(MockHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public HttpResponse execute(HttpHost target, HttpRequest request,
            HttpContext context) {
        Log.v("HTTP", request.getRequestLine().toString());

        HttpResponse response = httpClient.processRequest(request);
        if (response == null) {
            throw new AssertionError("Request \"" + request.getRequestLine().toString()
                    + "\" did not match any known pattern");
        }

        Log.v("HTTP", response.getStatusLine().toString());
        return response;
    }

}

/** The HTTP Response returned by a MockHttpServer */
class MockHttpResponse extends BasicHttpResponse {
    public MockHttpResponse(int statusCode, String responseBody, String contentType) {
        super(new ProtocolVersion("HTTP", 1, 1),
                statusCode,
                EnglishReasonPhraseCatalog.INSTANCE.getReason(
                        statusCode, Locale.getDefault()));

        if (responseBody != null) {
            try {
                StringEntity responseBodyEntity = new StringEntity(responseBody);
                if (contentType != null) {
                    responseBodyEntity.setContentType(contentType);
                }
                this.setEntity(responseBodyEntity);
            } catch (UnsupportedEncodingException e) {
                // Nothing, really...
            }
        }
    }
}
