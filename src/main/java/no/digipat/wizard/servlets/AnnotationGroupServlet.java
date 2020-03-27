package no.digipat.wizard.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.mongodb.MongoClient;

import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.mongodb.dao.MongoAnnotationGroupDAO;

@WebServlet(urlPatterns = "/annotationGroup")
public class AnnotationGroupServlet extends HttpServlet {
    
    /**
     * Creates an annotation group. The request body must contain
     * a JSON object with the IDs of the annotations:
     * 
     * <pre>
     *   {
     *     "annotations": [&lt;long&gt;, &lt;long&gt;, ..., &lt;long&gt;],
     *     "name": &lt;string&gt;
     *   }
     * </pre>
     * 
     * The response body will contain a JSON object with the ID of the created group:
     * 
     * <pre>
     *   {
     *     "groupId": &lt;string&gt;
     *   }
     * </pre>
     * 
     * @param request the request
     * @param response the response
     * 
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AnnotationGroup group;
        try {
            group = inputToAnnotationGroup(request.getInputStream());
        } catch (JSONException | NullPointerException | ClassCastException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        ServletContext context = getServletContext();
        String databaseName = (String) context.getAttribute("MONGO_DATABASE");
        MongoClient client = (MongoClient) context.getAttribute("MONGO_CLIENT");
        MongoAnnotationGroupDAO dao = new MongoAnnotationGroupDAO(client, databaseName);
        String groupId = dao.createAnnotationGroup(group);
        
        JSONObject responseJson = new JSONObject();
        responseJson.put("groupId", groupId);
        response.setContentType("application/json");
        response.getWriter().print(responseJson);
    }
    
    private static AnnotationGroup inputToAnnotationGroup(InputStream inputStream)
            throws JSONException, NullPointerException, ClassCastException {
        JSONObject requestJson = new JSONObject(new JSONTokener(inputStream));
        JSONArray annotationIds = requestJson.getJSONArray("annotations");
        String name = requestJson.getString("name");
        Long[] idArray = annotationIds.toList().stream().<Long>map(id -> {
            // The JSON array will represent integers using the Integer class, unless they're too big, in which
            // case it uses the Long class
            if (id instanceof Long) {
                return (Long) id;
            } else {
                return (long) (int) id;
            }
        }).toArray(Long[]::new);
        return new AnnotationGroup().setAnnotationIds(Arrays.asList(idArray))
                .setCreationDate(new Date()).setName(name);
    }
    
}
