package no.digipat.wizard.servlets;

import static org.junit.Assert.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.mongodb.MongoClient;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.models.CSVCreator;
import no.digipat.wizard.models.results.AnalysisComponent;
import no.digipat.wizard.models.results.AnalysisResult;
import no.digipat.wizard.models.results.AnalysisValue;
import no.digipat.wizard.models.results.AnnotationGroupResults;
import no.digipat.wizard.models.results.Results;
import no.digipat.wizard.mongodb.dao.MongoAnnotationGroupDAO;
import no.digipat.wizard.mongodb.dao.MongoResultsDAO;

@RunWith(JUnitParamsRunner.class)
public class ExportAnalysisResultsTest {
    
    private static URL baseUrl;
    private static String databaseName;
    private static MongoClient client;
    private WebConversation conversation;
    private static final String groupId = "aaaaaaaaaaaaaaaaaaaaaaaa";
    private static AnnotationGroupResults groupResults;
    
    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();
    // TODO remove the folder after CSVCreator has been updated
    
    @BeforeClass
    public static void setUpClass() {
        baseUrl = IntegrationTests.getBaseUrl();
        databaseName = IntegrationTests.getDatabaseName();
        client = IntegrationTests.getMongoClient();
        groupResults = new AnnotationGroupResults()
                .setGroupId(groupId)
                .setAnnotations(Arrays.asList(
                    createResults("he"),
                    createResults("yo")
                ));
        MongoAnnotationGroupDAO groupDao = new MongoAnnotationGroupDAO(client, databaseName);
        AnnotationGroup group = new AnnotationGroup()
                .setGroupId(groupId)
                .setAnnotationIds(new ArrayList<Long>())
                .setCreationDate(new Date())
                .setName("group name")
                .setProjectId(20L);
        groupDao.createAnnotationGroup(group);
        MongoResultsDAO resultsDao = new MongoResultsDAO(client, databaseName);
        resultsDao.createAnnotationGroupResults(groupResults);
    }
    
    @Before
    public void setUp() {
        conversation = new WebConversation();
    }
    
    private static Results createResults(String analysisType) {
        AnalysisValue value1 = new AnalysisValue().setName("mean").setVal(-0.333f);
        AnalysisValue value2 = new AnalysisValue().setName("std").setVal(-0.111f);
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
        AnalysisResult analysisResults = new AnalysisResult().setName(analysisType)
                .setComponents(new ArrayList<AnalysisComponent>() {
                    {
                        add(analysisComponent1);
                        add(analysisComponent2);
                    }
                });
        Results results = new Results().setAnnotationId(3L)
                .setResults(new ArrayList<AnalysisResult>() {
                    {
                        add(analysisResults);
                    }
                });
        return results;
    }
    
    @AfterClass
    public static void tearDownClass() {
        client.getDatabase(databaseName).drop();
    }
    
    @Test
    @Parameters({
        "bbbbbbbbbbbbbbbbbbbbbbbb",
        "ooo"
    })
    public void testStatusCode404(String groupId) throws Exception {
        WebRequest request = new GetMethodWebRequest(baseUrl,
                "exportAnalysisResults?groupId=" + groupId);
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
    @Parameters(method = "getAnalyzeTypes")
    public void testExportResults(String analyzeType) throws Exception {
        WebRequest request = new GetMethodWebRequest(baseUrl, "exportAnalysisResults");
        request.setParameter("groupId", groupResults.getGroupId());
        if (analyzeType != null) {
            request.setParameter("analyzeType", analyzeType);
        }
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(200, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
        JSONObject json = new JSONObject(response.getText());
        assertEquals(
            CSVCreator.toCSV(groupResults.getAnnotations(),
                    folder.getRoot().getAbsolutePath(), analyzeType),
            json.getString("data")
        );
    }
    
    private static String[] getAnalyzeTypes() {
        return new String[] {null, "he", "yo"};
    }
    
}
