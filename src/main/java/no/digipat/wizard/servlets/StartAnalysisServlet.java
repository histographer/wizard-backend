package no.digipat.wizard.servlets;

import com.mongodb.MongoClient;
import no.digipat.wizard.models.AnalysisPostBody;
import no.digipat.wizard.models.AnalysisInformation;
import no.digipat.wizard.models.AnnotationGroup;
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
import java.nio.charset.StandardCharsets;
import java.util.UUID;

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
            AnalysisPostBody analysisPostBody = AnalysisPostBody.fromJsonString(requestJson);
            AnnotationGroup annotationGroup = dao.getAnnotationGroup(analysisPostBody.getGroupId());
            AnalysisInformation info = new AnalysisInformation().setStatus(AnalysisInformation.Status.PENDING)
                    .setAnnotationGroupId(analysisPostBody.getGroupId());
            String id = analysisInformationDao.createAnalysisInformation(info);
            info.setAnalysisId(id);
            response.getWriter().print(new JSONObject(info));
            if(annotationGroup == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            analysisPostBody.setAnnotations(annotationGroup.getAnnotationIds());
        } catch (IllegalArgumentException| NullPointerException | ClassCastException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
        }
        response.setStatus(HttpServletResponse.SC_ACCEPTED);

        // TODO send message to analyze backend
    }
}
