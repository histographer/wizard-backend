package no.digipat.wizard.mongodb.dao;

import no.digipat.wizard.models.results.AnnotationGroupResults;
import no.digipat.wizard.models.results.AnalysisResult;
import no.digipat.wizard.models.results.Results;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mongodb.MongoClient;

import junitparams.JUnitParamsRunner;
import no.digipat.wizard.mongodb.DatabaseUnitTests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class MongoResultsDAOTest {

    private static MongoClient client;
    private static String databaseName;
    private MongoResultsDAO dao;
    private String analysisResultJsonString;
    private String analysisResultJsonStringNoGroupId;
    private String analysisResultJsonStringNoResults;
    private String analysisResultJsonStringResultsNoLength;
    private AnnotationGroupResults annotationGroupResults;

    @BeforeClass
    public static void setUpClass() {
        client = DatabaseUnitTests.getMongoClient();
        databaseName = DatabaseUnitTests.getDatabaseName();
    }

    @Before
    public void setUp() {
        dao = new MongoResultsDAO(client, databaseName);
        analysisResultJsonString = "{\"analysisId\":32,\"csvBase64\":\"abc\",\"annotations\":[{\"annotationId\":1,\"results\":{\"he\":{\"H\":{\"mean\":-0.4941735897897377,\"std\":0.04383346025184383},\"E\":{\"mean\":0.20421988842343536,\"std\":0.012792263926458863}}}}]}";
        analysisResultJsonStringNoGroupId = "{\"annotations\":[{\"annotationId\":1,\"results\":[{\"type\": \"he\",\"values\":{\"hematoxylin\":180,\"eosin\": 224}}]}]}";
        analysisResultJsonStringNoGroupId = "{\"csvBase64\":\"abc\",\"annotations\":[{\"annotationId\":1,\"results\":{\"he\":{\"H\":{\"mean\":-0.4941735897897377,\"std\":0.04383346025184383},\"E\":{\"mean\":0.20421988842343536,\"std\":0.012792263926458863}}}}]}";
        analysisResultJsonStringNoResults = "{\"analysisId\":\"testGroupdId\"";
        analysisResultJsonStringResultsNoLength = "{\"analysisId\":\"testGroupdId\",\"results\":[]}]}";

        Results res3 = createResults("he", 0.33f);
        annotationGroupResults = new AnnotationGroupResults().setAnalysisId("1").setAnnotations(new ArrayList<Results>() {{ add(res3); }});
    }


    private Results createResults(String resName, Float value) {
        Map<String, Float> values = new HashMap<>();
        values.put("mean", 0.333f);
        values.put("std", 0.555f);
        Map<String, Map<String, Float>> tempres= new HashMap<>();
        tempres.put("H", values);
        tempres.put("E", values);
        Map<String, Map<String, Map<String, Float>>> results= new HashMap<>();
        results.put("HE", tempres);
        return new Results().setAnnotationId(1l).setResults(results);
    }

    private AnnotationGroupResults createAnnotationGroupResultsForTests() {
        Results res3 = createResults("yo", 30f);
        Results res4 = createResults("HE", -0.99f);
        return new AnnotationGroupResults().setAnalysisId("1ask").setAnnotations(new ArrayList<Results>() {{ add(res3); add(res4);}});
    }



    @Test(expected=NullPointerException.class)
    public void jsonToAnnotationGroupResultsNoAnalysisId() {
        dao.jsonToAnnotationGroupResults(analysisResultJsonStringNoGroupId);
    }
    @Test(expected=RuntimeException.class)
    public void jsonToAnnotationGroupResultsNoResults() {
        dao.jsonToAnnotationGroupResults(analysisResultJsonStringNoResults);
    }
    @Test(expected=NullPointerException.class)
    public void jsonToAnnotationGroupResultsNull() {
        dao.jsonToAnnotationGroupResults(null);
    }
    @Test(expected=IllegalArgumentException.class)
    public void jsonToAnnotationGroupResultsEmptyString() {
        dao.jsonToAnnotationGroupResults("");
    }
    @Test(expected=IllegalArgumentException.class)
    public void jsonToAnnotationGroupResultsResultsNoLength() {
        dao.jsonToAnnotationGroupResults(analysisResultJsonStringResultsNoLength);
    }

    @Test
    public void jsonToAnnotationGroupResults() {
        AnnotationGroupResults res = dao.jsonToAnnotationGroupResults(analysisResultJsonString);
        System.out.println(res);
    }

    @Test
    public void createAnnotationGroupResults() {
        dao.createAnnotationGroupResults(annotationGroupResults);
    }

    @Test(expected=IllegalArgumentException.class)
    public void createAnnotationGroupResultsWithNull() {
        dao.createAnnotationGroupResults(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void validationTestAnnotationGroupFail() {
       AnnotationGroupResults res = createAnnotationGroupResultsForTests();
       res.setAnalysisId("");
       dao.validateAnnotationGroupResults(res);
    }
    @Test(expected=IllegalArgumentException.class)
    public void validationTestAnnotationResultsFail() {
        AnnotationGroupResults res = createAnnotationGroupResultsForTests();
        res.setAnnotations(new ArrayList<>());
        dao.validateAnnotationGroupResults(res);
    }

    @Test(expected=IllegalArgumentException.class)
    public void validationTestAnnotationResultsResultsAnnotationIdFail() {
        AnnotationGroupResults res = createAnnotationGroupResultsForTests();
        res.getAnnotations().get(0).setAnnotationId(null);
        dao.validateAnnotationGroupResults(res);
    }
    @Test(expected=IllegalArgumentException.class)
    public void validationTestAnnotationResultsResultListFail() {
        AnnotationGroupResults res = createAnnotationGroupResultsForTests();
        res.getAnnotations().get(0).setResults(new HashMap<String, Map<String, Map<String, Float>>>());
        dao.validateAnnotationGroupResults(res);
    }


    @Test
    public void validationTestSuccess() {
        AnnotationGroupResults res = createAnnotationGroupResultsForTests();
        dao.validateAnnotationGroupResults(res);
    }

    @Test(expected=IllegalArgumentException.class)
    public void createAnnotationGroupResultsFail() {
        AnnotationGroupResults res = createAnnotationGroupResultsForTests();
        res.setAnalysisId("");
        dao.createAnnotationGroupResults(res);
    }

    @Test
    public void createAnnotationGroupResultsSuccess() {

        AnnotationGroupResults res = createAnnotationGroupResultsForTests();
        dao.createAnnotationGroupResults(res);
        AnnotationGroupResults res1 = dao.getResults(res.getAnalysisId());
        System.out.println(res1);
    }

    @Test
    public void getResultsSuccess() {
        AnnotationGroupResults agr = createAnnotationGroupResultsForTests();
        dao.createAnnotationGroupResults(agr);
        AnnotationGroupResults res = dao.getResults(agr.getAnalysisId());
        assertEquals(agr, res);
    }

    @Test
    public void getNonexistentResults() {
        AnnotationGroupResults agr = createAnnotationGroupResultsForTests();
        dao.createAnnotationGroupResults(agr);
        assertNull(dao.getResults("THIS IS NOT VALID"));
    }
    
    @Test(expected=IllegalStateException.class)
    public void testCreateResultsWithDuplicateAnalysisId() throws Exception {
        AnnotationGroupResults results = createAnnotationGroupResultsForTests();
        dao.createAnnotationGroupResults(results);
        dao.createAnnotationGroupResults(results);
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
}
