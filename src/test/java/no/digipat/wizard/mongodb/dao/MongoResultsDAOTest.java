package no.digipat.wizard.mongodb.dao;

import no.digipat.wizard.models.results.*;
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

        analysisResultJsonString = "{\"analysisId\":\"aaaaaaaaaaaaaaaaa\",\"annotations\":[{\"annotationId\":1064743,\"results\":[{\"name\":\"HE\",\"components\":[{\"name\":\"H\",\"components\":[{\"name\":\"mean\",\"val\":-0.44739692704068174},{\"name\":\"std\",\"val\":0.08928628449947514}]}]}]}]}";
        //analysisResultJsonString = "{\"analysisId\":32,\"csvBase64\":\"abc\",\"annotations\":[{\"annotationId\":1,\"results\":{\"he\":{\"H\":{\"mean\":-0.4941735897897377,\"std\":0.04383346025184383},\"E\":{\"mean\":0.20421988842343536,\"std\":0.012792263926458863}}}}]}";
        analysisResultJsonStringNoGroupId = "{\"annotations\":[{\"annotationId\":1064743,\"results\":[{\"name\":\"HE\",\"components\":[{\"name\":\"H\",\"components\":[{\"name\":\"mean\",\"val\":-0.44739692704068174},{\"name\":\"std\",\"val\":0.08928628449947514}]}]}]}]}";
        analysisResultJsonStringNoResults = "{\"analysisId\":\"testGroupdId\"";
        analysisResultJsonStringResultsNoLength = "{\"analysisId\":\"testGroupdId\",\"results\":[]}]}";

        Results res3 = createResults("he", 0.33f);
        annotationGroupResults = new AnnotationGroupResults().setGroupId("1").setAnnotations(new ArrayList<Results>() {{ add(res3); }});
    }


    private Results createResults(String resName, Float value) {
        AnalysisValue value1 = new AnalysisValue().setName("mean").setVal(-0.333f);
        AnalysisValue value2 = new AnalysisValue().setName("std").setVal(-0.333f);
        AnalysisComponent analysisComponent1 = new AnalysisComponent().setName("H").setComponents(new ArrayList(){{add(value1); add(value2);}});
        AnalysisComponent analysisComponent2 = new AnalysisComponent().setName("E").setComponents(new ArrayList(){{add(value1); add(value2);}});
        AnalysisResult analysisResults = new AnalysisResult().setName("HE").setComponents(new ArrayList(){{add(analysisComponent1); add(analysisComponent2);}});
        Results results = new Results().setAnnotationId(3l).setResults(new ArrayList(){{add(analysisResults);}});
        return results;
    }

    private AnnotationGroupResults createAnnotationGroupResultsForTests() {
        Results res3 = createResults("yo", 30f);
        Results res4 = createResults("HE", -0.99f);
        return new AnnotationGroupResults().setGroupId("1ask").setAnnotations(new ArrayList<Results>() {{ add(res3); add(res4);}});
    }



    @Test(expected=NullPointerException.class)
    public void jsonToAnnotationGroupResultsNoAnalysisId() {
        dao.jsonToAnnotationGroupResultsRequestBody(analysisResultJsonStringNoGroupId);
    }
    @Test(expected=RuntimeException.class)
    public void jsonToAnnotationGroupResultsNoResults() {
        dao.jsonToAnnotationGroupResultsRequestBody(analysisResultJsonStringNoResults);
    }
    @Test(expected=NullPointerException.class)
    public void jsonToAnnotationGroupResultsNull() {
        dao.jsonToAnnotationGroupResultsRequestBody(null);
    }
    @Test(expected=IllegalArgumentException.class)
    public void jsonToAnnotationGroupResultsEmptyString() {
        dao.jsonToAnnotationGroupResultsRequestBody("");
    }
    @Test(expected=IllegalArgumentException.class)
    public void jsonToAnnotationGroupResultsResultsNoLength() {
        dao.jsonToAnnotationGroupResultsRequestBody(analysisResultJsonStringResultsNoLength);
    }

    @Test
    public void jsonToAnnotationGroupResults() {
        AnnotationGroupResultsRequestBody res = dao.jsonToAnnotationGroupResultsRequestBody(analysisResultJsonString);
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
       res.setGroupId("");
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
        res.getAnnotations().get(0).setResults(new ArrayList<>());
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
        res.setGroupId("");
        dao.createAnnotationGroupResults(res);
    }

    @Test
    public void createAnnotationGroupResultsSuccess() {

        AnnotationGroupResults res = createAnnotationGroupResultsForTests();
        dao.createAnnotationGroupResults(res);
        AnnotationGroupResults res1 = dao.getResults(res.getGroupId());
        System.out.println(res1);
    }

    @Test
    public void getResultsSuccess() {
        AnnotationGroupResults agr = createAnnotationGroupResultsForTests();
        dao.createAnnotationGroupResults(agr);
        AnnotationGroupResults res = dao.getResults(agr.getGroupId());
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
