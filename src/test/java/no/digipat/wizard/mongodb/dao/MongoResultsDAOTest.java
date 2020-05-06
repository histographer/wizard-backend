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
import java.util.List;

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

        analysisResultJsonString = "{\"analysisId\":\"aaaaaaaaaaaaaaaaa\","
                + "\"annotations\":[{\"annotationId\":1064743,"
                + "\"results\":[{\"name\":\"HE\",\"components\":["
                + "{\"name\":\"H\",\"components\":[{\"name\":\"mean\","
                + "\"val\":-0.44739692704068174},{\"name\":\"std\","
                + "\"val\":0.08928628449947514}]}]}]}]}";
        analysisResultJsonStringNoGroupId = "{\"annotations\":[{\"annotationId\":1064743,"
                + "\"results\":[{\"name\":\"HE\",\"components\":[{\"name\":\"H\","
                + "\"components\":[{\"name\":\"mean\",\"val\":-0.44739692704068174},"
                + "{\"name\":\"std\",\"val\":0.08928628449947514}]}]}]}]}";
        analysisResultJsonStringResultsNoLength = "{\"analysisId\":\"testGroupdId\","
                + "\"results\":[]}]}";

        Results res3 = createResults("he", 1L);
        annotationGroupResults = new AnnotationGroupResults()
                .setGroupId("1")
                .setAnnotations(new ArrayList<Results>() {
                    {
                        add(res3);
                    }
                });
    }


    private Results createResults(String resName, Long id) {
        AnalysisValue value1 = new AnalysisValue().setName("mean").setVal(-0.333f);
        AnalysisValue value2 = new AnalysisValue().setName("std").setVal(-0.333f);
        AnalysisComponent analysisComponent1 = new AnalysisComponent()
                .setName("H")
                .setComponents(new ArrayList<AnalysisValue>() {
                    {
                        add(value1);
                        add(value2);
                    }
                });
        AnalysisComponent analysisComponent2 = new AnalysisComponent()
                .setName("E")
                .setComponents(new ArrayList<AnalysisValue>() {
                    {
                        add(value1);
                        add(value2);
                    }
                });
        AnalysisResult analysisResults = new AnalysisResult()
                .setName(resName)
                .setComponents(new ArrayList<AnalysisComponent>() {
                    {
                        add(analysisComponent1);
                        add(analysisComponent2);
                    }
                });
        Results results = new Results()
                .setAnnotationId(id)
                .setResults(new ArrayList<AnalysisResult>() {
                    {
                        add(analysisResults);
                    }
                });
        return results;
    }

    private AnnotationGroupResults createAnnotationGroupResultsForTests(String groupId) {
        Results res3 = createResults("yo", 31L);
        Results res4 = createResults("HE", 32L);
        return new AnnotationGroupResults()
                .setGroupId(groupId)
                .setAnnotations(new ArrayList<Results>() {
                    {
                        add(res3);
                        add(res4);
                    }
                });
    }



    @Test(expected = NullPointerException.class)
    public void jsonToAnnotationGroupResultsNoAnalysisId() {
        MongoResultsDAO.jsonToAnnotationGroupResultsRequestBody(
                analysisResultJsonStringNoGroupId);
    }
    @Test(expected = RuntimeException.class)
    public void jsonToAnnotationGroupResultsNoResults() {
        MongoResultsDAO.jsonToAnnotationGroupResultsRequestBody(
                analysisResultJsonStringNoResults);
    }
    @Test(expected = NullPointerException.class)
    public void jsonToAnnotationGroupResultsNull() {
        MongoResultsDAO.jsonToAnnotationGroupResultsRequestBody(null);
    }
    @Test(expected = IllegalArgumentException.class)
    public void jsonToAnnotationGroupResultsEmptyString() {
        MongoResultsDAO.jsonToAnnotationGroupResultsRequestBody("");
    }
    @Test(expected = IllegalArgumentException.class)
    public void jsonToAnnotationGroupResultsResultsNoLength() {
        MongoResultsDAO .jsonToAnnotationGroupResultsRequestBody(
                analysisResultJsonStringResultsNoLength);
    }

    @Test
    public void jsonToAnnotationGroupResults() {
        MongoResultsDAO.jsonToAnnotationGroupResultsRequestBody(analysisResultJsonString);
    }

    @Test
    public void createAnnotationGroupResults() {
        dao.createAnnotationGroupResults(annotationGroupResults);
    }

    @Test
    public void createAndUpdateGroupResultsOneOnly() {
        dao.createAndUpdateResults(annotationGroupResults);
        AnnotationGroupResults res = dao.getResults(annotationGroupResults.getGroupId());
        assertEquals(res.getGroupId(), annotationGroupResults.getGroupId());
        assertEquals(res.getAnnotations(), annotationGroupResults.getAnnotations());
    }

    @Test
    public void createAndUpdateGroupResultsTwoEquals() {
        dao.createAndUpdateResults(annotationGroupResults);
        dao.createAndUpdateResults(annotationGroupResults);
        AnnotationGroupResults res = dao.getResults(annotationGroupResults.getGroupId());
        assertEquals(res.getGroupId(), annotationGroupResults.getGroupId());
        assertEquals(res.getAnnotations().get(0).getResults().size(),
                annotationGroupResults.getAnnotations().get(0).getResults().size());
    }


    @Test
    public void createAndUpdateGroupResultsTwoPlusOne() {
        System.out.println("0");
        System.out.println(annotationGroupResults);
        dao.createAndUpdateResults(annotationGroupResults);
        AnnotationGroupResults res = dao.getResults(annotationGroupResults.getGroupId());
        assertEquals(res.getAnnotations().get(0).getResults().size(),
                annotationGroupResults.getAnnotations().get(0).getResults().size());

        List<Results> annotations = annotationGroupResults.getAnnotations();
        annotations.add(createResults("AB", 1L));
        dao.createAndUpdateResults(annotationGroupResults.setAnnotations(annotations));

        res = dao.getResults(annotationGroupResults.getGroupId());
        assertEquals(2, res.getAnnotations().get(0).getResults().size());
    }
    @Test(expected = IllegalArgumentException.class)
    public void createAnnotationGroupResultsWithNull() {
        dao.createAnnotationGroupResults(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validationTestAnnotationGroupFail() {
        AnnotationGroupResults res = createAnnotationGroupResultsForTests("abc");
        res.setGroupId("");
        dao.validateAnnotationGroupResults(res);
    }
    @Test(expected = IllegalArgumentException.class)
    public void validationTestAnnotationResultsFail() {
        AnnotationGroupResults res = createAnnotationGroupResultsForTests("abc");
        res.setAnnotations(new ArrayList<>());
        dao.validateAnnotationGroupResults(res);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validationTestAnnotationResultsResultsAnnotationIdFail() {
        AnnotationGroupResults res = createAnnotationGroupResultsForTests("abc");
        res.getAnnotations().get(0).setAnnotationId(null);
        dao.validateAnnotationGroupResults(res);
    }
    @Test(expected = IllegalArgumentException.class)
    public void validationTestAnnotationResultsResultListFail() {
        AnnotationGroupResults res = createAnnotationGroupResultsForTests("abc");
        res.getAnnotations().get(0).setResults(new ArrayList<>());
        dao.validateAnnotationGroupResults(res);
    }


    @Test
    public void validationTestSuccess() {
        AnnotationGroupResults res = createAnnotationGroupResultsForTests("abc");
        dao.validateAnnotationGroupResults(res);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createAnnotationGroupResultsFail() {
        AnnotationGroupResults res = createAnnotationGroupResultsForTests("abc");
        res.setGroupId("");
        dao.createAnnotationGroupResults(res);
    }

    @Test
    public void createAnnotationGroupResultsSuccess() {

        AnnotationGroupResults res = createAnnotationGroupResultsForTests("abc");
        dao.createAnnotationGroupResults(res);
        AnnotationGroupResults res1 = dao.getResults(res.getGroupId());
    }

    @Test
    public void getResultsSuccess() {
        AnnotationGroupResults agr = createAnnotationGroupResultsForTests("abc");
        dao.createAnnotationGroupResults(agr);
        AnnotationGroupResults res = dao.getResults(agr.getGroupId());
        assertEquals(agr, res);
    }

    @Test
    public void getNonexistentResults() {
        AnnotationGroupResults agr = createAnnotationGroupResultsForTests("abc");
        dao.createAnnotationGroupResults(agr);
        assertNull(dao.getResults("THIS IS NOT VALID"));
    }
    
    @Test(expected = IllegalStateException.class)
    public void testCreateResultsWithDuplicateGroupId() throws Exception {
        AnnotationGroupResults results = createAnnotationGroupResultsForTests("abc");
        dao.createAnnotationGroupResults(results);
        dao.createAnnotationGroupResults(results);
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
}
