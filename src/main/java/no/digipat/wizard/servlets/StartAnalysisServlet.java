package no.digipat.wizard.servlets;

import com.mongodb.MongoClient;
import no.digipat.wizard.models.startanalysis.AnalysisPostBody;
import no.digipat.wizard.models.AnalysisInformation;
import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.models.startanalysis.AnalysisPostRequest;
import no.digipat.wizard.models.startanalysis.CallbackURLs;
import no.digipat.wizard.mongodb.dao.MongoAnalysisInformationDAO;
import no.digipat.wizard.mongodb.dao.MongoAnnotationGroupDAO;
import no.digipat.wizard.servlets.util.Analysis;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@WebServlet(urlPatterns = "/startAnalysis")
public class StartAnalysisServlet extends HttpServlet {
    
    // TODO DOCS
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String requestJson = IOUtils.toString(request.getInputStream(),
                request.getCharacterEncoding());
        AnalysisPostRequest analysisPostRequest = AnalysisPostRequest.fromJsonString(requestJson);
        
        AnnotationGroup annotationGroup;
        try {
            annotationGroup = getAnnotationGroup(analysisPostRequest);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid or missing group ID");
            return;
        }
        if (annotationGroup == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        String analysisId = createAndGetAnalysisInformationId(annotationGroup.getGroupId());
        
        AnalysisPostBody analysisPostBody = createAnalysisPostBody(analysisId,
                annotationGroup, analysisPostRequest);
        try {
            AnalysisPostBody.validate(analysisPostBody);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        Analysis.getAnalysisPostResponse(
                (URL) getServletContext().getAttribute("ANALYSIS_URL"),
                "analysis/analyze/",
                AnalysisPostBody.toJsonString(analysisPostBody),
                202
        );
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
        response.getWriter().print(new JSONObject().put("analysisId", analysisId));
    }
    
    private AnnotationGroup getAnnotationGroup(AnalysisPostRequest analysisPostRequest)
            throws IllegalArgumentException, IOException {
        ServletContext context = getServletContext();
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        MongoAnnotationGroupDAO groupDao = new MongoAnnotationGroupDAO(client, databaseName);
        return groupDao.getAnnotationGroup(analysisPostRequest.getGroupId());
    }
    
    private String createAndGetAnalysisInformationId(String groupId) {
        ServletContext context = getServletContext();
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        MongoAnalysisInformationDAO analysisInformationDao =
                new MongoAnalysisInformationDAO(client, databaseName);
        AnalysisInformation info = new AnalysisInformation()
                .setStatus(AnalysisInformation.Status.PENDING)
                .setAnnotationGroupId(groupId);
        return analysisInformationDao.createAnalysisInformation(info);
    }
    
    private AnalysisPostBody createAnalysisPostBody(String analysisId,
            AnnotationGroup annotationGroup, AnalysisPostRequest analysisPostRequest)
                    throws ServletException {
        URL wizardURL = (URL) getServletContext().getAttribute("WIZARD_BACKEND_URL");
        try {
            return new AnalysisPostBody()
                .setAnalysisId(analysisId)
                .setProjectId(annotationGroup.getProjectId())
                .setAnnotations(annotationGroup.getAnnotationIds())
                .setAnalysis(analysisPostRequest.getAnalysis())
                .setCallbackURLs(
                    new CallbackURLs()
                        .setAnalysisResult(new URL(wizardURL, "analysisResults"))
                        .setUpdateStatus(new URL(wizardURL, "analysisInformation"))
                );
        } catch (MalformedURLException e) {
            throw new ServletException(e);
        }
    }
    
}
