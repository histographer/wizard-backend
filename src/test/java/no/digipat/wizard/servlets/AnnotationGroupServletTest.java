package no.digipat.wizard.servlets;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.mongodb.MongoClient;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.mongodb.dao.MongoAnnotationGroupDAO;

@RunWith(JUnitParamsRunner.class)
public class AnnotationGroupServletTest {
    
    private static URL baseUrl;
    private static String databaseName;
    private static MongoClient client;
    private MongoAnnotationGroupDAO dao;
    private WebConversation conversation;
    
    @BeforeClass
    public static void setUpClass() {
        baseUrl = IntegrationTests.getBaseUrl();
        databaseName = IntegrationTests.getDatabaseName();
        client = IntegrationTests.getMongoClient();
    }
    
    @Before
    public void setUp() {
        dao = new MongoAnnotationGroupDAO(client, databaseName);
        conversation = new WebConversation();
    }
    
    private static PostMethodWebRequest createPostRequest(String path, String messageBody, String contentType) throws Exception {
        return new PostMethodWebRequest(new URL(baseUrl, path).toString(),
                new ByteArrayInputStream(messageBody.getBytes("UTF-8")), contentType);
    }
    
    @Test
    @Parameters(method="getInvalidGroupCreationRequestBodies")
    public void testStatusCode400OnGroupCreation(String messageBody) throws Exception {
        WebRequest request = createPostRequest("annotationGroup", messageBody, "application/json");
        WebResponse response = conversation.getResponse(request);
        
        assertEquals("Testing with message body: " + messageBody + ".", 400, response.getResponseCode());
    }
    
    private static String[] getInvalidGroupCreationRequestBodies() {
        return new String[] {
                "this is not JSON",
                "{}",
                "{\"annotations\": \"not an array\"}",
                "{\"annotations\": [\"not a number\"]}",
                "{\"annotations\": null}",
                "{\"annotations\": [null]}"
        };
    }
    
    @Test
    public void testCreateAnnotationGroup() throws Exception {
        JSONObject requestJson = new JSONObject();
        List<Long> annotationIds = Arrays.asList(new Long[] {42L, 1337L, Long.MAX_VALUE});
        requestJson.put("annotations", annotationIds);
        WebRequest request = createPostRequest("annotationGroup", requestJson.toString(), "application/json");
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(200, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
        JSONObject responseJson = new JSONObject(response.getText());
        String groupId = responseJson.getString("groupId");
        assertNotNull(groupId);
        AnnotationGroup group = dao.getAnnotationGroup(groupId);
        assertNotNull(group);
        assertEquals(annotationIds, group.getAnnotationIds());
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
}
