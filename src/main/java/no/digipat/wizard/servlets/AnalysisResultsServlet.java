package no.digipat.wizard.servlets;

import com.mongodb.MongoClient;
import no.digipat.wizard.models.AnalysisInformation;
import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.models.results.AnnotationGroupResults;
import no.digipat.wizard.models.results.AnnotationGroupResultsRequestBody;
import no.digipat.wizard.mongodb.dao.MongoAnalysisInformationDAO;
import no.digipat.wizard.mongodb.dao.MongoAnnotationGroupDAO;
import no.digipat.wizard.mongodb.dao.MongoResultsDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

@WebServlet(urlPatterns = "/analysisResults")
public class AnalysisResultsServlet extends HttpServlet {
    
    /**
     * Creates a new analysis result.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String databaseName = getDatabaseName();
        MongoClient client = getDatabaseClient();
        MongoResultsDAO resultsDao = new MongoResultsDAO(client, databaseName);
        MongoAnalysisInformationDAO analysisInfoDao =
                new MongoAnalysisInformationDAO(client, databaseName);
        String requestJson = IOUtils.toString(request.getReader());
        AnnotationGroupResultsRequestBody results;
        try {
            results = MongoResultsDAO.jsonToAnnotationGroupResultsRequestBody(requestJson);
        } catch (IllegalArgumentException | NullPointerException | IllegalStateException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
            return;
        }
        AnalysisInformation info = analysisInfoDao.getAnalysisInformation(results.getAnalysisId());
        if (info == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            try {
                AnnotationGroupResults annotationGroupResults = new AnnotationGroupResults()
                        .setGroupId(info.getAnnotationGroupId())
                        .setAnnotations(results.getAnnotations());
                resultsDao.createAnnotationGroupResults(annotationGroupResults);
            } catch (IllegalStateException e) { // Duplicate analysis ID
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            analysisInfoDao.updateStatus(results.getAnalysisId(),
                    AnalysisInformation.Status.SUCCESS);
            response.setStatus(HttpServletResponse.SC_CREATED);
        }
    }
    
    /**
     * Gets the analysis results for an annotation group, as determined by the query
     * parameter {@code groupId}.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        MongoResultsDAO resultsDao = new MongoResultsDAO(getDatabaseClient(), getDatabaseName());
        MongoAnnotationGroupDAO groupDao =
            new MongoAnnotationGroupDAO(getDatabaseClient(), getDatabaseName());
        String groupId = request.getParameter("groupId");
        if (groupId == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        AnnotationGroupResults results = resultsDao.getResults(groupId);
        AnnotationGroup group;
        try {
            group = groupDao.getAnnotationGroup(groupId);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid annotation group ID");
            return;
        }
        if (results == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            response.getWriter().print(
                    MongoResultsDAO.annotationGroupResultsToJson(results, group.getName())
            );
        }
    }
    
    private String getDatabaseName() {
        return (String) getServletContext().getAttribute("MONGO_DATABASE");
    }
    
    private MongoClient getDatabaseClient() {
        return (MongoClient) getServletContext().getAttribute("MONGO_CLIENT");
    }
    
}
