package no.digipat.wizard.servlets;

import static org.junit.Assert.*;

import java.net.URL;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.mongodb.MongoClient;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import no.digipat.wizard.models.AnalysisStatus;
import no.digipat.wizard.models.AnalysisStatus.Status;
import no.digipat.wizard.mongodb.dao.MongoAnalysisStatusDAO;

@RunWith(JUnitParamsRunner.class)
public class AnalysisStatusServletTest {
    
    private static final String hexId = "abcdef0123456789abcdef12";
    private static URL baseUrl;
    private static String databaseName;
    private static MongoClient client;
    private MongoAnalysisStatusDAO dao;
    private WebConversation conversation;
    
    @BeforeClass
    public static void setUpClass() {
        baseUrl = IntegrationTests.getBaseUrl();
        databaseName = IntegrationTests.getDatabaseName();
        client = IntegrationTests.getMongoClient();
    }
    
    @Before
    public void setUp() {
        dao = new MongoAnalysisStatusDAO(client, databaseName);
        conversation = new WebConversation();
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
    @Test
    @Parameters(method="getStatusValues")
    public void testGetStatus(Status status) throws Exception {
        String analysisId = dao.createAnalysisStatus(
                new AnalysisStatus()
                    .setAnnotationGroupId(hexId)
                    .setStatus(status)
        );
        WebRequest request = new GetMethodWebRequest(baseUrl, "analysisStatus?analysisId=" + analysisId);
        
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(response.getText(), 200, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
        JSONObject json = new JSONObject(response.getText());
        assertEquals(status.name().toLowerCase(), json.getString("status"));
    }
    
    private static Status[] getStatusValues() {
        return Status.values();
    }
    
    @Test
    public void testStatusCode404() throws Exception {
        WebRequest request = new GetMethodWebRequest(baseUrl, "analysisStatus?analysisId=" + hexId);
        
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(404, response.getResponseCode());
    }
    
    @Test
    @Parameters({
        "analysisStatus",
        "analysisStatus?analysisId=",
        "analysisStatus?analysisId=oooooooooooooooooooooooo",
        "analysisStatus?analysisId=abc",
    })
    public void testStatusCode400(String path) throws Exception {
        WebRequest request = new GetMethodWebRequest(baseUrl, path);
        
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(400, response.getResponseCode());
    }
    
}
