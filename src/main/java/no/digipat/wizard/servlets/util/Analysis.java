package no.digipat.wizard.servlets.util;

import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

/**
 * Utility methods for communicating with the analysis API.
 * 
 * @author Kent Are Torvik
 * @author Jon Wallem Anundsen
 *
 */
public final class Analysis {
    
    private Analysis() {
        throw new UnsupportedOperationException("This is a utility class.");
    }
    
    /**
     * Sends a POST request to the analysis API with a given
     * as the body, returning the response.
     * 
     * @param baseUrl the base URL of the analysis API, e.g. {@code http://example.com/api}.
     * @param path the relative path of the request
     * @param requestBody the request body, which should be a JSON object
     * @param acceptableStatusCodes the status codes that it is acceptable for
     * the analysis API to return. If empty, only the code 200 is acceptable.
     * 
     * @return the response
     * 
     * @throws IOException if an I/O error occurs. In particular, if
     * the analysis API returns an unacceptable status code.
     */
    public static HttpResponse getAnalysisPostResponse(URL baseUrl, String path,
            String requestBody, int... acceptableStatusCodes) throws IOException {
        HttpResponse response = Request.Post(new URL(baseUrl, path).toString())
            .setHeader("Accept", "application/json")
            .bodyString(requestBody, ContentType.create("application/json"))
            .execute().returnResponse();
        if (acceptableStatusCodes.length == 0) {
            acceptableStatusCodes = new int[] {200};
        }
        int responseCode = response.getStatusLine().getStatusCode();
        if (!ArrayUtils.contains(acceptableStatusCodes, responseCode)) {
            throw new IOException("Analysis API returned unacceptable status code: "
                    + responseCode);
        }
        return response;
    }
}
