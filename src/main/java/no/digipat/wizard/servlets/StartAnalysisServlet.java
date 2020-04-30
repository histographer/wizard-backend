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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet(urlPatterns = "/startAnalysis")
public class StartAnalysisServlet extends HttpServlet {

    // TODO DOCS
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletContext context = getServletContext();
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        MongoAnnotationGroupDAO dao = new MongoAnnotationGroupDAO(client, databaseName);
        MongoAnalysisInformationDAO analysisInformationDao =
                new MongoAnalysisInformationDAO(client, databaseName);
        AnalysisPostBody analysisPostBody = null;
        try {
            String requestJson = IOUtils.toString(request.getInputStream(), "UTF8");
            AnalysisPostRequest analysisPostRequest =
                    AnalysisPostRequest.fromJsonString(requestJson);
            AnnotationGroup annotationGroup = dao.getAnnotationGroup(
                    analysisPostRequest.getGroupId());
            AnalysisInformation info = new AnalysisInformation()
                    .setStatus(AnalysisInformation.Status.PENDING)
                    .setAnnotationGroupId(analysisPostRequest.getGroupId());
            String id = analysisInformationDao.createAnalysisInformation(info);
            info.setAnalysisId(id);
            if (annotationGroup == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            URL wizardURL = (URL) context.getAttribute("WIZARD_BACKEND_URL");
            analysisPostBody = new AnalysisPostBody().setAnalysisId(id)
                    .setProjectId(annotationGroup.getProjectId())
                    .setAnnotations(annotationGroup.getAnnotationIds())
                    .setAnalysis(analysisPostRequest.getAnalysis())
                    .setCallbackURLs(
                            new CallbackURLs()
                            .setAnalysisResults(wizardURL + "/analysisResults")
                            .setUpdateStatus(wizardURL + "/analysisInformation"));

            AnalysisPostBody.validate(analysisPostBody);
        } catch (IllegalArgumentException | NullPointerException | ClassCastException e) {
            context.log(e.toString());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
        }

        if (analysisPostBody == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Something went wrong, the post body is null at StartAnalysisServlet."
                    + " Failure in WIZARD_BACKEND");
        }

        URL analysisURL = (URL) context.getAttribute("ANALYSIS_URL");
        analysisURL = new URL(analysisURL, "analysis/analyze/");
        String requestJsonString = AnalysisPostBody.toJsonString(analysisPostBody);
        context.log("StartAnalysisServlet tries to connect to: " + analysisURL.toString());
        context.log("With body: " + requestJsonString);
        HttpURLConnection connection = (HttpURLConnection) analysisURL.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        try (PrintWriter writer = new PrintWriter(connection.getOutputStream())) {
            writer.print(requestJsonString);
            writer.flush();
        }
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            response.sendError(responseCode, "Expected response code 200 from analysis,"
                    + " but got " + responseCode);
        }
        response.setStatus(HttpServletResponse.SC_ACCEPTED);
    }
}
