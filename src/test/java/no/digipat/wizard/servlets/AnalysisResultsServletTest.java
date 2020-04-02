package no.digipat.wizard.servlets;

import com.meterware.httpunit.*;
import com.mongodb.MongoClient;
import junitparams.JUnitParamsRunner;
import no.digipat.wizard.models.AnalysisInformation;
import no.digipat.wizard.models.AnalysisInformation.Status;
import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.models.results.AnalysisResult;
import no.digipat.wizard.models.results.AnnotationGroupResults;
import no.digipat.wizard.models.results.Results;
import no.digipat.wizard.mongodb.dao.MongoAnalysisInformationDAO;
import no.digipat.wizard.mongodb.dao.MongoAnnotationGroupDAO;
import no.digipat.wizard.mongodb.dao.MongoResultsDAO;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class AnalysisResultsServletTest {
    
    private static URL baseUrl;
    private static String databaseName;
    private static MongoClient client;
    private MongoAnnotationGroupDAO groupDao;
    private WebConversation conversation;
    private String analyzeBodyValid;
    private String analyzeBodyInvalid;
    private MongoResultsDAO resultDao;
    private MongoAnalysisInformationDAO infoDao;

    @BeforeClass
    public static void setUpClass() {
        baseUrl = IntegrationTests.getBaseUrl();
        databaseName = IntegrationTests.getDatabaseName();
        client = IntegrationTests.getMongoClient();
    }
    
    @Before
    public void setUp() {
        groupDao = new MongoAnnotationGroupDAO(client, databaseName);
        resultDao = new MongoResultsDAO(client, databaseName);
        infoDao = new MongoAnalysisInformationDAO(client, databaseName);
        conversation = new WebConversation();
        analyzeBodyInvalid = "{\"annotations\":[\"1\",\"2\",\"3\"],\"analysis\":[\"he\",\"rgb\"]}";
        analyzeBodyValid = "{\"analysisId\":\"aaaaaaaaaaaaaaaaaaaaaaaa\",\"annotations\":[{\"annotationId\":2,\"results\":[{\"type\": \"he\",\"values\":{\"hematoxylin\":180,\"eosin\": 224}}]}]}";
    }
    
    private static PostMethodWebRequest createPostRequest(String path, String messageBody, String contentType) throws Exception {
        return new PostMethodWebRequest(new URL(baseUrl, path).toString(),
                new ByteArrayInputStream(messageBody.getBytes("UTF-8")), contentType);
    }
    
    @Test
    public void testStatusCode404OnNonexistentAnalysis() throws Exception {
        WebRequest request = createPostRequest("analysisResults", analyzeBodyValid, "application/json");
        
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(404, response.getResponseCode());
    }
    
    @Test
    public void testStatusCode400OnInvalidPost() throws Exception {
        WebRequest request = createPostRequest("analysisResults",analyzeBodyInvalid, "application/json");
        WebResponse response = conversation.getResponse(request);
        assertEquals("Testing with message body: " + analyzeBodyInvalid + ".", 400, response.getResponseCode());
    }
    
    @Test
    public void testStatusCode202OnValidPost() throws Exception {
        String grpId = "cccccccccccccccccccccccc";
        AnnotationGroup group1 = new AnnotationGroup()
                .setGroupId(grpId)
                .setAnnotationIds(Arrays.asList(1L, 2L))
                .setCreationDate(new Date())
                .setName("group 1")
                .setProjectId(20L);
        groupDao.createAnnotationGroup(group1);
        infoDao.createAnalysisInformation(new AnalysisInformation().setAnalysisId("aaaaaaaaaaaaaaaaaaaaaaaa").setAnnotationGroupId(grpId).setStatus(AnalysisInformation.Status.PENDING));
        WebRequest request = createPostRequest("analysisResults",analyzeBodyValid, "application/json");
        WebResponse response = conversation.getResponse(request);
        System.out.println(IOUtils.toString(response.getInputStream(), StandardCharsets.UTF_8));
        assertEquals("Testing with message body: " + analyzeBodyValid + ".", 201, response.getResponseCode());
        AnnotationGroup grp = groupDao.getAnnotationGroup(grpId);
        assertNotEquals(grp, null);
        AnnotationGroupResults agr = resultDao.getResults("aaaaaaaaaaaaaaaaaaaaaaaa");
        AnalysisInformation info = infoDao.getAnalysisInformation("aaaaaaaaaaaaaaaaaaaaaaaa");
        assertEquals(info.getStatus(), AnalysisInformation.Status.SUCCESS);
        assertEquals(MongoResultsDAO.jsonToAnnotationGroupResults(analyzeBodyValid), agr);
    }
    
    @Test
    public void testStatusCode400OnDuplicateAnalysisId() throws Exception {
        infoDao.createAnalysisInformation(
                new AnalysisInformation()
                .setAnalysisId("aaaaaaaaaaaaaaaaaaaaaaaa")
                .setAnnotationGroupId("bbbbbbbbbbbbbbbbbbbbbbbb")
                .setStatus(Status.PENDING)
        );
        WebRequest request = createPostRequest("analysisResults", analyzeBodyValid, "application/json");
        conversation.sendRequest(request);
        
        try {
            WebResponse response = conversation.getResponse(request);
            assertEquals(400, response.getResponseCode());
        } catch (IOException e) {
            // Workaround for an annoying bug that sporadically makes conversation.getResponse
            // (and similar methods) throw IOException when the response code is 400,
            // even if conversation.getExceptionsThrownOnErrorStatus() is false
            assertTrue(e.getMessage().contains("400")); // Kind of a hack, but there's not much else we can do
        }
    }
    
    @Test
    public void testStatusCode400OnInvalidGet() throws Exception {
        WebRequest request = new GetMethodWebRequest(baseUrl, "analysisResults");
        
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(400, response.getResponseCode());
    }
    
    @Test
    public void testStatusCode404OnGettingNonexistentResults() throws Exception {
        WebRequest request = new GetMethodWebRequest(baseUrl, "analysisResults?analysisId=aaaaaaaaaaaaaaaaaaaaaaaa");
        
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(404, response.getResponseCode());
    }
    
    @Test
    public void testGetResults() throws Exception {
        AnnotationGroupResults results = new AnnotationGroupResults()
                .setAnalysisId("aaaaaaaaaaaaaaaaaaaaaaaa")
                .setAnnotations(Arrays.asList(
                        new Results().setAnnotationId(1L).setAnalysisResults(Arrays.asList(
                                new AnalysisResult().setType("he").setValues(new HashMap<String, Integer>() {{
                                    put("hematoxylin", 180);
                                    put("eosin", 224);
                                }})
                        )),
                        new Results().setAnnotationId(2L).setAnalysisResults(Arrays.asList(
                                new AnalysisResult().setType("he").setValues(new HashMap<String, Integer>() {{
                                    put("hematoxylin", 150);
                                    put("eosin", 200);
                                }}),
                                new AnalysisResult().setType("cool-analysis").setValues(new HashMap<String, Integer>() {{
                                    put("elite", 1337);
                                }})
                        ))
                ));
        resultDao.createAnnotationGroupResults(results);
        
        WebRequest request = new GetMethodWebRequest(baseUrl, "analysisResults?analysisId=aaaaaaaaaaaaaaaaaaaaaaaa");
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(200, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
        AnnotationGroupResults receivedResults = MongoResultsDAO.jsonToAnnotationGroupResults(response.getText());
        assertEquals(results, receivedResults);
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
}
