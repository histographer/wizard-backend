package no.digipat.wizard.servlets;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.digipat.wizard.models.CSVcreator;
import no.digipat.wizard.models.results.AnnotationGroupResults;
import no.digipat.wizard.mongodb.dao.MongoResultsDAO;
import org.json.JSONObject;

import com.mongodb.MongoClient;

import no.digipat.wizard.models.CsvResult;
import no.digipat.wizard.mongodb.dao.MongoCsvResultDAO;

@WebServlet("/exportAnalysisResults")
public class ExportAnalysisResultsServlet extends HttpServlet {
    
    /**
     * Gets the results of a given analysis (as determined by the query
     * parameter {@code analysisId}) in CSV format.
     * Appropriate for downloading results to a file.
     * <p>
     * Response:
     * </p>
     * 
     * <pre>
     * {
     *   "data": &lt;CSV string&gt;
     * }
     * </pre>
     * 
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String groupId = request.getParameter("groupId");
        String analyzeType = request.getParameter("analyzeType");
        //String path = (String) getServletContext().getAttribute("STORAGE_PATH");
        String path = getServletContext().getRealPath("/");;
        if (groupId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
        MongoResultsDAO dao = getDao();
        AnnotationGroupResults results = dao.getResults(groupId);
        String base64 = null;
        if(analyzeType == null) {
            try {
                base64 = CSVcreator.toCSV(results.getAnnotations(), path, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                base64 = CSVcreator.toCSV(results.getAnnotations(), path, analyzeType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(base64 == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            JSONObject responseJson = new JSONObject();
            responseJson.put("data", base64);
            response.getWriter().print(responseJson);
        }
    }
    
    private MongoResultsDAO getDao() {
        ServletContext context = getServletContext();
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        return new MongoResultsDAO(client, databaseName);
    }
    
}
