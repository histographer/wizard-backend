package no.digipat.wizard.servlets;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.*;

import static java.util.Comparator.comparing;

import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.mongodb.dao.MongoAnnotationGroupDAO;
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
    private MongoAnalysisInformationDAO infoDao;
    private MongoAnnotationGroupDAO groupDao;
    private WebConversation conversation;
    private String annotationGroupId;
    
    @BeforeClass
    public static void setUpClass() {
        baseUrl = IntegrationTests.getBaseUrl();
        databaseName = IntegrationTests.getDatabaseName();
        client = IntegrationTests.getMongoClient();
    }
    
    @Before
    public void setUp() {
        infoDao = new MongoAnalysisInformationDAO(client, databaseName);
        groupDao = new MongoAnnotationGroupDAO(client, databaseName);
        conversation = new WebConversation();
        AnnotationGroup annotationGroup = new AnnotationGroup()
                .setName("Test")
                .setAnnotationIds(new ArrayList<Long>() {
                    {
                        add(1L);
                    }
                })
                .setCreationDate(new Date())
                .setProjectId(3L);
        annotationGroupId =  groupDao.createAnnotationGroup(annotationGroup);
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
    @Test
    @Parameters(method = "getStatusValues")
    public void testGetInformationForOneAnalysis(Status status) throws Exception {
        AnalysisInformation info = new AnalysisInformation()
                .setAnnotationGroupId(annotationGroupId)
                .setStatus(status)
                .setGroupName("Test");


        String analysisId = infoDao.createAnalysisInformation(info);
        WebRequest request = new GetMethodWebRequest(baseUrl,
                "analysisInformation?analysisId=" + analysisId);
        
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
        WebRequest request = new GetMethodWebRequest(baseUrl,
                "analysisInformation?analysisId=" + hexId);
        
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
                .setAnnotationGroupId(annotationGroupId)
                .setStatus(Status.PENDING);
        infoDao.createAnalysisInformation(info1);
        AnalysisInformation info2 = new AnalysisInformation()
                .setAnalysisId("bbbbbbbbbbbbbbbbbbbbbbbb")
                .setAnnotationGroupId(annotationGroupId)
                .setStatus(Status.SUCCESS);
        infoDao.createAnalysisInformation(info2);
        AnalysisInformation info3 = new AnalysisInformation()
                .setAnnotationGroupId("0123456789abcdef01234567")
                .setStatus(Status.FAILURE);
        infoDao.createAnalysisInformation(info3);
        
        WebRequest request = new GetMethodWebRequest(baseUrl,
                "analysisInformation?annotationGroupId=" + annotationGroupId);
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
