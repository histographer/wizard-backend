package no.digipat.wizard.servlets;

import com.meterware.httpunit.*;
import com.mongodb.MongoClient;
import junitparams.JUnitParamsRunner;
import no.digipat.wizard.models.AnalysisStatus;
import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.models.results.AnnotationGroupResults;
import no.digipat.wizard.mongodb.dao.MongoAnalysisStatusDAO;
import no.digipat.wizard.mongodb.dao.MongoAnnotationGroupDAO;
import no.digipat.wizard.mongodb.dao.MongoResultsDAO;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class AnalysisResultsServletTest {
    
    private static URL baseUrl;
    private static String databaseName;
    private static MongoClient client;
    private MongoAnnotationGroupDAO dao;
    private WebConversation conversation;
    private String analyzeBodyValid;
    private String analyzeBodyInvalid;
    private MongoResultsDAO resultDao;
    private MongoAnalysisStatusDAO statusDao;

    @BeforeClass
    public static void setUpClass() {
        baseUrl = IntegrationTests.getBaseUrl();
        databaseName = IntegrationTests.getDatabaseName();
        client = IntegrationTests.getMongoClient();
    }
    
    @Before
    public void setUp() {
        dao = new MongoAnnotationGroupDAO(client, databaseName);
        resultDao = new MongoResultsDAO(client, databaseName);
        statusDao = new MongoAnalysisStatusDAO(client, databaseName);
        conversation = new WebConversation();
        analyzeBodyInvalid = "{\"annotations\":[\"1\",\"2\",\"3\"],\"analysis\":[\"he\",\"rgb\"]}";
        analyzeBodyValid = "{\"analysisId\":\"aaaaaaaaaaaaaaaaaaaaaaaa\",\"annotations\":[{\"annotationId\":2,\"results\":[{\"type\": \"he\",\"values\":{\"hematoxylin\":180,\"eosin\": 224}}]}]}";
    }
    
    private static PostMethodWebRequest createPostRequest(String path, String messageBody, String contentType) throws Exception {
        return new PostMethodWebRequest(new URL(baseUrl, path).toString(),
                new ByteArrayInputStream(messageBody.getBytes("UTF-8")), contentType);
    }
    
    @Test
    public void testStatusCode400OnInvalidInput() throws Exception {
        WebRequest request = createPostRequest("analysisResults",analyzeBodyInvalid, "application/json");
        WebResponse response = conversation.getResponse(request);
        assertEquals("Testing with message body: " + analyzeBodyInvalid + ".", 400, response.getResponseCode());
    }


    @Test
    public void testStatusCode202OnValidInput() throws Exception {
        String grpId = "cccccccccccccccccccccccc";
        AnnotationGroup group1 = new AnnotationGroup()
                .setGroupId(grpId)
                .setAnnotationIds(Arrays.asList(1L, 2L))
                .setCreationDate(new Date())
                .setName("group 1")
                .setProjectId(20L);
        dao.createAnnotationGroup(group1);
        statusDao.createAnalysisStatus(new AnalysisStatus().setAnalysisId("aaaaaaaaaaaaaaaaaaaaaaaa").setAnnotationGroupId(grpId).setStatus(AnalysisStatus.Status.PENDING));
        WebRequest request = createPostRequest("analysisResults",analyzeBodyValid, "application/json");
        WebResponse response = conversation.getResponse(request);
        System.out.println(IOUtils.toString(response.getInputStream(), StandardCharsets.UTF_8));
        assertEquals("Testing with message body: " + analyzeBodyValid + ".", 201, response.getResponseCode());
        AnnotationGroup grp = dao.getAnnotationGroup(grpId);
        assertNotEquals(grp, null);
        List<AnnotationGroupResults> agr = resultDao.getResults("aaaaaaaaaaaaaaaaaaaaaaaa");
        System.out.println(agr);
        assertEquals(agr.size(), 1);
        AnalysisStatus status = statusDao.getAnalysisStatus("aaaaaaaaaaaaaaaaaaaaaaaa");
        assertEquals(status.getStatus(), AnalysisStatus.Status.SUCCESS);
    }

    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
}
