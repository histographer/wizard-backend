package no.digipat.wizard.servlets;

import com.mongodb.MongoClient;
import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.models.results.AnnotationGroupResults;
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = getServletContext();
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        MongoAnnotationGroupDAO MAGdao = new MongoAnnotationGroupDAO(client, databaseName);
        MongoResultsDAO ResultsDao = new MongoResultsDAO(client, databaseName);
        try {
            String requestJson = IOUtils.toString(request.getReader());
            AnnotationGroupResults results = MongoResultsDAO.jsonToAnnotationGroupResults(requestJson);
            AnnotationGroup annotationGroup = MAGdao.getAnnotationGroup(results.getAnalysisId());
            if(annotationGroup == null) {
                throw new IllegalArgumentException("Annotation group does not exist in the database");
            }
            ResultsDao.createAnnotationGroupResults(results);
        } catch (IllegalArgumentException| NullPointerException | ClassCastException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
        } catch (RuntimeException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
        }
        response.setStatus(HttpServletResponse.SC_CREATED);
    }
}
