package no.digipat.wizard.servlets;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.mongodb.MongoClient;

import no.digipat.wizard.models.CsvResult;
import no.digipat.wizard.mongodb.dao.MongoCsvResultDAO;

@WebServlet("/exportAnalysisResults")
public class ExportAnalysisResultsServlet extends HttpServlet {
    
    /**
     * Gets the results of a given analysis (as determined by the query
     * parameter {@code analysisId}) in CSV format, encoded in base 64.
     * Appropriate for downloading results to a file.
     * <p>
     * Response:
     * </p>
     * 
     * <pre>
     * {
     *   "data": &lt;base 64 string&gt;
     * }
     * </pre>
     * 
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String analysisId = request.getParameter("analysisId");
        if (analysisId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            MongoCsvResultDAO dao = getDao();
            CsvResult csvResult = dao.getCsvResult(analysisId);
            if (csvResult == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                JSONObject responseJson = new JSONObject();
                responseJson.put("data", csvResult.getData());
                response.setContentType("application/json");
                response.getWriter().print(responseJson);
            }
        }
    }
    
    private MongoCsvResultDAO getDao() {
        ServletContext context = getServletContext();
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        return new MongoCsvResultDAO(client, databaseName);
    }
    
}
