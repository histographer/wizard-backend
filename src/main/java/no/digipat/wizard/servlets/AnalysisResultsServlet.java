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
        ServletContext context = getServletContext();
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        MongoResultsDAO ResultsDao = new MongoResultsDAO(client, databaseName);
        MongoAnalysisStatusDAO analysisStatusDao = new MongoAnalysisStatusDAO(client, databaseName);
        try {
            String requestJson = IOUtils.toString(request.getReader());
            AnnotationGroupResults results = MongoResultsDAO.jsonToAnnotationGroupResults(requestJson);
            AnalysisStatus status = analysisStatusDao.getAnalysisStatus(results.getAnalysisId());
            if(status == null) {
                throw new IllegalArgumentException("AnalysisStatus does not exist in the database");
            }
            ResultsDao.createAnnotationGroupResults(results);
            analysisStatusDao.updateStatus(results.getAnalysisId(), AnalysisStatus.Status.SUCCESS);

        } catch (IllegalArgumentException| NullPointerException | ClassCastException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
        } catch (RuntimeException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.toString());
        }
        response.setStatus(HttpServletResponse.SC_CREATED);
    }
}
