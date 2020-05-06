package no.digipat.wizard.servlets;

import com.meterware.httpunit.*;
import com.mongodb.MongoClient;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import no.digipat.wizard.models.AnalysisInformation;
import no.digipat.wizard.models.AnalysisInformation.Status;
import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.models.results.*;
import no.digipat.wizard.mongodb.dao.MongoAnalysisInformationDAO;
import no.digipat.wizard.mongodb.dao.MongoAnnotationGroupDAO;
import no.digipat.wizard.mongodb.dao.MongoResultsDAO;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.net.URL;
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
        analyzeBodyInvalid = "{\"annotations\":[\"1\",\"2\",\"3\"],"
                + "\"analysis\":[\"he\",\"rgb\"]}";
        analyzeBodyValid = "{\"analysisId\":\"aaaaaaaaaaaaaaaaaaaaaaaa\","
                + "\"annotations\":[{\"annotationId\":1064743,"
                + "\"results\":[{\"name\":\"HE\","
                + "\"components\":[{\"name\":\"H\",\"components\":["
                + "{\"name\":\"mean\",\"val\":-0.44739692704068174},"
                + "{\"name\":\"std\",\"val\":0.08928628449947514}]}]}]}]}";
    }
    
    private static PostMethodWebRequest createPostRequest(String path,
            String messageBody, String contentType) throws Exception {
        return new PostMethodWebRequest(new URL(baseUrl, path).toString(),
                new ByteArrayInputStream(messageBody.getBytes("UTF-8")), contentType);
    }

    private Results createResults(String resName, Float value) {
        AnalysisValue value1 = new AnalysisValue().setName("mean").setVal(-0.333f);
        AnalysisValue value2 = new AnalysisValue().setName("std").setVal(-0.333f);
        AnalysisComponent analysisComponent1 = new AnalysisComponent().setName("H")
                .setComponents(new ArrayList<AnalysisValue>() {
                    {
                        add(value1);
                        add(value2);
                    }
                });
        AnalysisComponent analysisComponent2 = new AnalysisComponent().setName("E")
                .setComponents(new ArrayList<AnalysisValue>() {
                    {
                        add(value1);
                        add(value2);
                    }
                });
        AnalysisResult analysisResults = new AnalysisResult().setName("HE")
                .setComponents(new ArrayList<AnalysisComponent>() {
                    {
                        add(analysisComponent1);
                        add(analysisComponent2);
                    }
                });
        Results results = new Results()
                .setAnnotationId(3L)
                .setResults(new ArrayList<AnalysisResult>() {
                    {
                        add(analysisResults);
                    }
                });
        return results;
    }

    @Test
    public void testStatusCode404OnPostToNonexistentAnalysis() throws Exception {
        WebRequest request = createPostRequest("analysisResults",
                analyzeBodyValid, "application/json");
        
        WebResponse response = conversation.getResponse(request);

        
        assertEquals(404, response.getResponseCode());
    }
    
    @Test
    public void testStatusCode400OnInvalidPost() throws Exception {
        WebRequest request = createPostRequest("analysisResults",
                analyzeBodyInvalid, "application/json");
        WebResponse response = conversation.getResponse(request);
        assertEquals("Testing with message body: " + analyzeBodyInvalid + ".",
                400, response.getResponseCode());
    }
    
    @Test
    public void testStatusCode202OnValidPost() throws Exception {
        String groupId = "cccccccccccccccccccccccc";
        String analysisId = "aaaaaaaaaaaaaaaaaaaaaaaa";
        AnnotationGroup group1 = new AnnotationGroup()
                .setGroupId(groupId)
                .setAnnotationIds(Arrays.asList(1L, 2L))
                .setCreationDate(new Date())
                .setName("group 1")
                .setProjectId(20L);
        groupDao.createAnnotationGroup(group1);
        infoDao.createAnalysisInformation(new AnalysisInformation()
                .setAnalysisId(analysisId).setAnnotationGroupId(groupId)
                .setStatus(AnalysisInformation.Status.PENDING));
        
        WebRequest request = createPostRequest("analysisResults",
                analyzeBodyValid, "application/json");
        WebResponse response = conversation.getResponse(request);
        
        assertEquals("Testing with message body: " + analyzeBodyValid + ".",
                201, response.getResponseCode());
        AnnotationGroupResults agr = resultDao.getResults(groupId);
        AnalysisInformation info = infoDao.getAnalysisInformation(analysisId);
        assertEquals(info.getStatus(), AnalysisInformation.Status.SUCCESS);
        AnnotationGroupResultsRequestBody res = MongoResultsDAO
                .jsonToAnnotationGroupResultsRequestBody(analyzeBodyValid);
        AnnotationGroupResults valid = new AnnotationGroupResults()
                .setGroupId(info.getAnnotationGroupId())
                .setAnnotations(res.getAnnotations());
        assertEquals(valid, agr);
    }
    
    @Test
    public void testStatusCode400OnDuplicateAnalysisId() throws Exception {
        infoDao.createAnalysisInformation(
                new AnalysisInformation()
                .setAnalysisId("aaaaaaaaaaaaaaaaaaaaaaaa")
                .setAnnotationGroupId("bbbbbbbbbbbbbbbbbbbbbbbb")
                .setStatus(Status.PENDING)
        );
        WebRequest request = createPostRequest("analysisResults",
                analyzeBodyInvalid, "application/json");
        conversation.sendRequest(request);
        
        // Workaround for HttpUnit throwing an exception on status code 400:
        HttpResponse response = Request.Post(new URL(baseUrl, "analysisResults").toString())
            .bodyString(analyzeBodyInvalid, ContentType.create("application/json"))
            .execute()
            .returnResponse();
        
        assertEquals(400, response.getStatusLine().getStatusCode());
    }
    
    @Test
    @Parameters({
        "",
        "?groupId=",
        "?groupId=ooo"
    })
    public void testStatusCode400OnInvalidGet(String queryString) throws Exception {
        WebRequest request = new GetMethodWebRequest(baseUrl, "analysisResults" + queryString);
        
        WebResponse response = conversation.getResponse(request);
        
        assertEquals("\n" + response.getText() + "\n", 400, response.getResponseCode());
    }
    
    @Test
    public void testStatusCode404OnGettingNonexistentResults() throws Exception {
        WebRequest request = new GetMethodWebRequest(baseUrl,
                "analysisResults?groupId=aaaaaaaaaaaaaaaaaaaaaaaa");
        
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(404, response.getResponseCode());
    }
    
    @Test
    public void testGetResults() throws Exception {
        AnnotationGroup group = new AnnotationGroup()
                .setCreationDate(new Date())
                .setName("group name")
                .setProjectId(20L)
                .setAnnotationIds(new ArrayList<Long>());
        String groupId = groupDao.createAnnotationGroup(group);
        groupDao.createAnnotationGroup(group);
        AnnotationGroupResults results = new AnnotationGroupResults()
                .setGroupId(groupId)
                .setAnnotations(Arrays.asList(
                        createResults("he", 0.44f),
                        createResults("bc", 0.3f)
                ));
        resultDao.createAnnotationGroupResults(results);
        
        WebRequest request = new GetMethodWebRequest(baseUrl,
                "analysisResults?groupId=" + groupId);
        WebResponse response = conversation.getResponse(request);
        
        assertEquals("\n" + response.getText() + "\n", 200, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
        AnnotationGroupResults receivedResults = MongoResultsDAO
                .jsonToAnnotationGroupResults(response.getText());
        assertEquals(results, receivedResults);
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
}
