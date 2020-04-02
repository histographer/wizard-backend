package no.digipat.wizard.servlets;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static java.util.Comparator.comparing;

import org.json.JSONArray;
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
import no.digipat.wizard.models.AnalysisInformation;
import no.digipat.wizard.models.AnalysisInformation.Status;
import no.digipat.wizard.mongodb.dao.MongoAnalysisInformationDAO;

@RunWith(JUnitParamsRunner.class)
public class AnalysisInformationServletTest {
    
    private static final String hexId = "abcdef0123456789abcdef12";
    private static URL baseUrl;
    private static String databaseName;
    private static MongoClient client;
    private MongoAnalysisInformationDAO dao;
    private WebConversation conversation;
    
    @BeforeClass
    public static void setUpClass() {
        baseUrl = IntegrationTests.getBaseUrl();
        databaseName = IntegrationTests.getDatabaseName();
        client = IntegrationTests.getMongoClient();
    }
    
    @Before
    public void setUp() {
        dao = new MongoAnalysisInformationDAO(client, databaseName);
        conversation = new WebConversation();
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
    @Test
    @Parameters(method="getStatusValues")
    public void testGetInformationForOneAnalysis(Status status) throws Exception {
        AnalysisInformation info = new AnalysisInformation()
                .setAnnotationGroupId(hexId)
                .setStatus(status);
        String analysisId = dao.createAnalysisInformation(info);
        WebRequest request = new GetMethodWebRequest(baseUrl, "analysisInformation?analysisId=" + analysisId);
        
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(response.getText(), 200, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
        AnalysisInformation retrievedInfo = AnalysisInformation.fromJson(response.getText());
        assertEquals(info.setAnalysisId(analysisId), retrievedInfo);
    }
    
    private static Status[] getStatusValues() {
        return Status.values();
    }
    
    @Test
    public void testStatusCode404OnNonexistentAnalysis() throws Exception {
        WebRequest request = new GetMethodWebRequest(baseUrl, "analysisInformation?analysisId=" + hexId);
        
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(404, response.getResponseCode());
    }
    
    @Test
    @Parameters({
        "analysisInformation",
        "analysisInformation?analysisId=",
        "analysisInformation?analysisId=oooooooooooooooooooooooo",
        "analysisInformation?analysisId=abc"
    })
    public void testStatusCode400(String path) throws Exception {
        WebRequest request = new GetMethodWebRequest(baseUrl, path);
        
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(400, response.getResponseCode());
    }
    
    @Test
    public void testGetAnalysisInfoForAnnotationGroup() throws Exception {
        AnalysisInformation info1 = new AnalysisInformation()
                .setAnalysisId("aaaaaaaaaaaaaaaaaaaaaaaa")
                .setAnnotationGroupId("abcdef0123456789abcdef12")
                .setStatus(Status.PENDING);
        dao.createAnalysisInformation(info1);
        AnalysisInformation info2 = new AnalysisInformation()
                .setAnalysisId("bbbbbbbbbbbbbbbbbbbbbbbb")
                .setAnnotationGroupId("abcdef0123456789abcdef12")
                .setStatus(Status.SUCCESS);
        dao.createAnalysisInformation(info2);
        AnalysisInformation info3 = new AnalysisInformation()
                .setAnnotationGroupId("0123456789abcdef01234567")
                .setStatus(Status.FAILURE);
        dao.createAnalysisInformation(info3);
        
        WebRequest request = new GetMethodWebRequest(baseUrl, "analysisInformation?annotationGroupId=abcdef0123456789abcdef12");
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(200, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
        JSONObject json = new JSONObject(response.getText());
        JSONArray array = json.getJSONArray("analyses");
        List<AnalysisInformation> retrievedAnalyses = new ArrayList<>();
        for (Object object : array) {
            JSONObject jsonObject = (JSONObject) object;
            retrievedAnalyses.add(AnalysisInformation.fromJson(jsonObject.toString()));
        }
        Collections.sort(retrievedAnalyses, comparing(analysis -> analysis.getAnalysisId()));
        assertEquals(Arrays.asList(info1, info2), retrievedAnalyses);
    }
    
}
