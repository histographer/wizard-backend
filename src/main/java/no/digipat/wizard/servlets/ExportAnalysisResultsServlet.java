package no.digipat.wizard.servlets;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.digipat.wizard.models.CSVCreator;
import no.digipat.wizard.models.results.AnnotationGroupResults;
import no.digipat.wizard.mongodb.dao.MongoResultsDAO;
import org.json.JSONObject;

import com.mongodb.MongoClient;

@WebServlet("/exportAnalysisResults")
public class ExportAnalysisResultsServlet extends HttpServlet {
    
    /**
     * Gets the analysis results of a given annotation group in CSV format.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String groupId = request.getParameter("groupId");
        String analyzeType = request.getParameter("analyzeType");
        if (groupId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        MongoResultsDAO dao = getDao();
        AnnotationGroupResults results = dao.getResults(groupId);
        if (results == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            String path = getServletContext().getRealPath("/");
            String base64 = CSVCreator.toCSV(results.getAnnotations(), path, analyzeType);
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
