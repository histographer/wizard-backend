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

import no.digipat.wizard.models.AnalysisStatus;
import no.digipat.wizard.mongodb.dao.MongoAnalysisStatusDAO;

@WebServlet("/analysisStatus")
public class AnalysisStatusServlet extends HttpServlet {
    
    /**
     * Gets the status of an analysis, as determined by the query
     * string parameter {@code analysisId}.
     * 
     * <p>
     * The response body will contain a JSON object of the following form,
     * where {@code status} is either {@code "pending"}, {@code "success"},
     * or {@code "failure"}:
     * </p>
     * 
     * <pre>
     * {
     *   "status": status
     * }
     * </pre>
     * 
     * 
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String analysisId = request.getParameter("analysisId");
        MongoAnalysisStatusDAO dao = getDao();
        try {
            AnalysisStatus analysisStatus = dao.getAnalysisStatus(analysisId);
            if (analysisStatus == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                JSONObject json = new JSONObject();
                json.put("status", analysisStatus.getStatus().name().toLowerCase());
                response.setContentType("application/json");
                response.getWriter().print(json);
            }
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private MongoAnalysisStatusDAO getDao() {
        ServletContext context = getServletContext();
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        return new MongoAnalysisStatusDAO(client, databaseName);
    }
    
}
