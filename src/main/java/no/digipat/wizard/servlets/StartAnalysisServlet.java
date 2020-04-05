package no.digipat.wizard.servlets;

import com.mongodb.MongoClient;
import no.digipat.wizard.models.startanalysis.AnalysisPostBody;
import no.digipat.wizard.models.AnalysisInformation;
import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.models.startanalysis.AnalysisPostRequest;
import no.digipat.wizard.models.startanalysis.CallbackURLs;
import no.digipat.wizard.mongodb.dao.MongoAnalysisInformationDAO;
import no.digipat.wizard.mongodb.dao.MongoAnnotationGroupDAO;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@WebServlet(urlPatterns = "/startAnalysis")
public class StartAnalysisServlet extends HttpServlet {

    // TODO DOCS
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = getServletContext();
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        MongoAnnotationGroupDAO dao = new MongoAnnotationGroupDAO(client, databaseName);
        MongoAnalysisInformationDAO analysisInformationDao = new MongoAnalysisInformationDAO(client, databaseName);
        try {
            String requestJson = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
            AnalysisPostRequest analysisPostRequest = AnalysisPostRequest.fromJsonString(requestJson);
            AnnotationGroup annotationGroup = dao.getAnnotationGroup(analysisPostRequest.getGroupId());
            AnalysisInformation info = new AnalysisInformation().setStatus(AnalysisInformation.Status.PENDING)
                    .setAnnotationGroupId(analysisPostRequest.getGroupId());
            String id = analysisInformationDao.createAnalysisInformation(info);
            info.setAnalysisId(id);
            if(annotationGroup == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            URL wizardURL = (URL) context.getAttribute("WIZARD_BACKEND_URL");
            AnalysisPostBody analysisPostBody = new AnalysisPostBody().setAnalysisId(id)
                    .setProjectId(annotationGroup.getProjectId())
                    .setAnnotations(annotationGroup.getAnnotationIds())
                    .setAnalysis(analysisPostRequest.getAnalysis())
                    .setCallbackURLs(new CallbackURLs().setAnalysisResults(wizardURL+"/analysisResults")
                            .setUpdateStatus(wizardURL+"/analysisInformation"));

            AnalysisPostBody.validate(analysisPostBody);
            response.getWriter().print(new JSONObject(info));
            // TODO send message to analyze backend

            //analysisPostBody.setAnnotations(annotationGroup.getAnnotationIds());
        } catch (IllegalArgumentException| NullPointerException | ClassCastException e) {
            context.log(e.toString());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
        }
        response.setStatus(HttpServletResponse.SC_ACCEPTED);

    }
}
