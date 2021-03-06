package no.digipat.wizard.servlets;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.mongodb.MongoClient;
import junitparams.JUnitParamsRunner;
import no.digipat.wizard.models.AnalysisInformation;
import no.digipat.wizard.models.AnalysisInformation.Status;
import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.mongodb.dao.MongoAnalysisInformationDAO;
import no.digipat.wizard.mongodb.dao.MongoAnnotationGroupDAO;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;

@RunWith(JUnitParamsRunner.class)
public class StartAnalysisServletTest {
    
    private static URL baseUrl;
    private static String databaseName;
    private static MongoClient client;
    private MongoAnnotationGroupDAO dao;
    private WebConversation conversation;
    private String analyzeBodyValid;
    private String analyzeBodyInvalid;
    private MongoAnalysisInformationDAO infoDao;

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
        analyzeBodyValid = "{\"groupId\":\"aaaaaaaaaaaaaaaaaaaaaaaa\","
                + "\"analysis\":[\"he\",\"rgb\"]}";
        analyzeBodyInvalid = "{\"annotations\":[\"1\",\"2\",\"3\"],"
                + "\"analysis\":[\"he\",\"rgb\"]}";
        infoDao = new MongoAnalysisInformationDAO(client, databaseName);

    }
    
    private static PostMethodWebRequest createPostRequest(String path,
            String messageBody, String contentType) throws Exception {
        return new PostMethodWebRequest(new URL(baseUrl, path).toString(),
                new ByteArrayInputStream(messageBody.getBytes("UTF-8")), contentType);
    }
    
    @Test
    public void testStatusCode400OnInvalidInput() throws Exception {
        WebRequest request = createPostRequest("startAnalysis", analyzeBodyInvalid,
                "application/json");
        WebResponse response = conversation.getResponse(request);
        assertEquals("Testing with message body: " + analyzeBodyInvalid + "."
                + " Got response: \n" + response.getText() + "\n",
                400, response.getResponseCode());
    }
    
    @Test
    public void testStatusCode404OnNonexistentAnnotationGroup() throws Exception {
        WebRequest request = createPostRequest("startAnalysis",
                analyzeBodyValid, "application/json");
        
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(404, response.getResponseCode());
    }
    
    @Test
    public void testStatusCode202OnValidInput() throws Exception {
        AnnotationGroup group1 = new AnnotationGroup()
                .setGroupId("aaaaaaaaaaaaaaaaaaaaaaaa")
                .setAnnotationIds(Arrays.asList(1L, 2L))
                .setCreationDate(new Date())
                .setName("group 1")
                .setProjectId(20L);
        dao.createAnnotationGroup(group1);
        
        WebRequest request = createPostRequest("startAnalysis",
                analyzeBodyValid, "application/json");
        WebResponse response = conversation.getResponse(request);
        
        assertEquals("Testing with message body: " + analyzeBodyValid + "."
                + "Got response body: \n" + response.getText() + "\n",
                202, response.getResponseCode());
        JSONObject jsonObject = new JSONObject(response.getText());
        AnalysisInformation info = infoDao.getAnalysisInformation(
                jsonObject.getString("analysisId"));
        assertNotNull(info);
        assertEquals(Status.PENDING, info.getStatus());
    }

    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
}
