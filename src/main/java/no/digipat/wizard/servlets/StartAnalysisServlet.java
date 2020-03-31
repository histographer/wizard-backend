package no.digipat.wizard.servlets;

import com.mongodb.MongoClient;
import no.digipat.wizard.models.AnalysisPostBody;
import no.digipat.wizard.models.AnalysisStatus;
import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.mongodb.dao.MongoAnalysisStatusDAO;
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
        MongoAnalysisStatusDAO analysisStatusDao = new MongoAnalysisStatusDAO(client, databaseName);
        try {
            String requestJson = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
            AnalysisPostBody analysisPostBody = AnalysisPostBody.fromJsonString(requestJson);
            AnnotationGroup annotationGroup = dao.getAnnotationGroup(analysisPostBody.getGroupId());
            String uuid = UUID.randomUUID().toString();
            AnalysisStatus status = new AnalysisStatus().setStatus(AnalysisStatus.Status.PENDING)
                    .setAnnotationGroupId(analysisPostBody.getGroupId());
            String id = analysisStatusDao.createAnalysisStatus(status);
            status.setAnalysisId(id);
            response.getWriter().print(new JSONObject(status));
            if(annotationGroup == null) {
                throw new IllegalArgumentException("Annotation group does not exist in the database");
            }
            analysisPostBody.setAnnotations(annotationGroup.getAnnotationIds());
        } catch (IllegalArgumentException| NullPointerException | ClassCastException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
        }
        response.setStatus(HttpServletResponse.SC_ACCEPTED);

        // TODO send message to analyze backend
    }
}
