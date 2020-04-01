package no.digipat.wizard.servlets;

import com.mongodb.MongoClient;
import no.digipat.wizard.models.AnalysisStatus;
import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.models.results.AnnotationGroupResults;
import no.digipat.wizard.mongodb.dao.MongoAnalysisStatusDAO;
import no.digipat.wizard.mongodb.dao.MongoAnnotationGroupDAO;
import no.digipat.wizard.mongodb.dao.MongoResultsDAO;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

@WebServlet(urlPatterns = "/analysisResults")
public class AnalysisResultsServlet extends HttpServlet {

    //TODO DOCS
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String databaseName = getDatabaseName();
        MongoClient client = getDatabaseClient();
        MongoResultsDAO ResultsDao = new MongoResultsDAO(client, databaseName);
        MongoAnalysisStatusDAO analysisStatusDao = new MongoAnalysisStatusDAO(client, databaseName);
        String requestJson = IOUtils.toString(request.getReader());
        AnnotationGroupResults results;
        try {
            results = MongoResultsDAO.jsonToAnnotationGroupResults(requestJson);
        } catch (IllegalArgumentException| NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
            return;
        }
        AnalysisStatus status = analysisStatusDao.getAnalysisStatus(results.getAnalysisId());
        if (status == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            ResultsDao.createAnnotationGroupResults(results);
            analysisStatusDao.updateStatus(results.getAnalysisId(), AnalysisStatus.Status.SUCCESS);
            response.setStatus(HttpServletResponse.SC_CREATED);
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MongoResultsDAO dao = new MongoResultsDAO(getDatabaseClient(), getDatabaseName());
        String analysisId = request.getParameter("analysisId");
        if (analysisId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        AnnotationGroupResults results = dao.getResults(analysisId);
        if (results == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.setContentType("application/json");
            response.getWriter().print(MongoResultsDAO.annotationGroupResultsToJson(results));
        }
    }
    
    private String getDatabaseName() {
        return (String) getServletContext().getAttribute("MONGO_DATABASE");
    }
    
    private MongoClient getDatabaseClient() {
        return (MongoClient) getServletContext().getAttribute("MONGO_CLIENT");
    }
    
}
