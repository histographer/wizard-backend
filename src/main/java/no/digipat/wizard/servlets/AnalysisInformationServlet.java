package no.digipat.wizard.servlets;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.mongodb.dao.MongoAnnotationGroupDAO;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.MongoClient;

import no.digipat.wizard.models.AnalysisInformation;
import no.digipat.wizard.mongodb.dao.MongoAnalysisInformationDAO;

@WebServlet("/analysisInformation")
public class AnalysisInformationServlet extends HttpServlet {
    
    /**
     * Gets information about an analysis (if the query parameter {@code analysisId}
     * is present), or about all analyses associated with a given annotation group
     * (if the query parameter {@code annotationGroupId} is present).
     * 
     * <p>
     * When getting information about a single analysis, the response body will
     * contain a JSON object of the following form, where {@code status} is either
     * {@code "pending"}, {@code "success"}, or {@code "failure"}:
     * </p>
     * 
     * <pre>
     * {
     *   "groupName": groupName,
     *   "analysisId": analysisId,
     *   "annotationGroupId": groupId
     *   "status": status
     * }
     * </pre>
     * 
     * <p>
     * When getting information about several analyses, the response body will contain
     * a JSON object of the following form, where {@code analyses} is an array of objects
     * of the same form as the one above:
     * </p>
     * 
     * <pre>
     * {
     *   "analyses": analyses
     * }
     * </pre>
     * 
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        MongoAnalysisInformationDAO dao = getDao();
        MongoAnnotationGroupDAO AGdao = getAnnotationGroupDao();
        String analysisId = request.getParameter("analysisId");
        String annotationGroupId = request.getParameter("annotationGroupId");
        try {
            if (analysisId != null) {
                AnalysisInformation analysisInformation = dao.getAnalysisInformation(analysisId);
                if (analysisInformation == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    AnnotationGroup annotationGroupObject = AGdao.getAnnotationGroup(analysisInformation.getAnnotationGroupId());
                    analysisInformation.setGroupName(annotationGroupObject.getName());
                    response.getWriter().print(analysisInformation.toJson());
                }
            } else if (annotationGroupId != null) {
                List<AnalysisInformation> analyses = dao.getAnalysisInformationForAnnotationGroup(annotationGroupId);
                JSONArray array = new JSONArray();
                for (AnalysisInformation info : analyses) {
                    array.put(new JSONObject(info.toJson()));
                }
                AnnotationGroup annotationGroupObject = AGdao.getAnnotationGroup(annotationGroupId);
                JSONObject responseJson = new JSONObject();
                responseJson.put("groupName", annotationGroupObject.getName());
                responseJson.put("analyses", array);
                response.getWriter().print(responseJson);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
    private MongoAnalysisInformationDAO getDao() {
        ServletContext context = getServletContext();
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        return new MongoAnalysisInformationDAO(client, databaseName);
    }

    private MongoAnnotationGroupDAO getAnnotationGroupDao() {
        ServletContext context = getServletContext();
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        return new MongoAnnotationGroupDAO(client, databaseName);
    }
    
}
