package no.digipat.wizard.servlets;

import static org.junit.Assert.*;

import java.net.URL;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.mongodb.MongoClient;

import no.digipat.wizard.models.CsvResult;
import no.digipat.wizard.mongodb.dao.MongoCsvResultDAO;

public class ExportAnalysisResultsTest {
    
    private static final CsvResult csvResult = new CsvResult()
            .setAnalysisId("abc")
            .setData("some data");
    private static URL baseUrl;
    private static String databaseName;
    private static MongoClient client;
    private WebConversation conversation;
    private MongoCsvResultDAO dao;
    
    @BeforeClass
    public static void setUpClass() {
        baseUrl = IntegrationTests.getBaseUrl();
        databaseName = IntegrationTests.getDatabaseName();
        client = IntegrationTests.getMongoClient();
    }
    
    @Before
    public void setUp() {
        dao = new MongoCsvResultDAO(client, databaseName);
        dao.createCsvResult(csvResult);
        conversation = new WebConversation();
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
    @Test
    public void testStatusCode404() throws Exception {
        WebRequest request = new GetMethodWebRequest(baseUrl,
                "exportAnalysisResults?analysisId=def");
        // Note that the analysis ID is different from that of
        // the result we've inserted into the database
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(404, response.getResponseCode());
    }
    
    @Test
    public void testStatusCode400() throws Exception {
        WebRequest request = new GetMethodWebRequest(baseUrl, "exportAnalysisResults");
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(400, response.getResponseCode());
    }
    
    @Test
    public void testExportResults() throws Exception {
        WebRequest request = new GetMethodWebRequest(baseUrl,
                "exportAnalysisResults?analysisId=abc");
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(200, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
        JSONObject json = new JSONObject(response.getText());
        assertEquals(csvResult.getData(), json.getString("data"));
    }
    
}
