package no.digipat.wizard.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONTokener;

@WebServlet("/availableAnalysisTypes")
public class AvailableAnalysesServlet extends HttpServlet {
    
    /**
     * Gets the names of the types of analysis that are available.
     * The response body has the following format:
     * 
     * <pre>
     * {
     *   "analysisTypes": [&lt;string&gt;, &lt;string&gt;, ..., &lt;string&gt;]
     * }
     * </pre>
     * 
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        URL baseUrl = (URL) getServletContext().getAttribute("ANALYSIS_URL");
        HttpURLConnection connection = (HttpURLConnection) new URL(baseUrl,
                "analysis/available/?format=json").openConnection();
        connection.setRequestProperty("Accept", "application/json");
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Expected response code 200 from analysis backend, but got "
                    + responseCode);
        }
        JSONObject jsonFromAnalysis;
        try (InputStream inputStream = connection.getInputStream()) {
            jsonFromAnalysis = new JSONObject(new JSONTokener(inputStream));
        }
        JSONObject jsonForResponse = new JSONObject();
        jsonForResponse.put("analysisTypes", jsonFromAnalysis.get("names"));
        response.getWriter().print(jsonForResponse);
    }
    
}
