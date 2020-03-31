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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.eq;
import static org.junit.Assert.assertEquals;

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
        analysisResultJsonString = "{\"analysisId\":\"testGroupdId\",\"annotations\":[{\"annotationId\":2,\"results\":[{\"type\": \"he\",\"values\":{\"hematoxylin\":180,\"eosin\": 224}}]}]}";
        analysisResultJsonStringNoGroupId = "{\"annotations\":[{\"annotationId\":1,\"results\":[{\"type\": \"he\",\"values\":{\"hematoxylin\":180,\"eosin\": 224}}]}]}";
        analysisResultJsonStringNoResults = "{\"analysisId\":\"testGroupdId\"";
        analysisResultJsonStringResultsNoLength = "{\"analysisId\":\"testGroupdId\",\"results\":[]}]}";
        AnalysisResult res1 = new AnalysisResult().setType("he")
                .setValues(new HashMap<String, Integer>(){{
                put("hemax", 32);
                put("coolcat", 32);
                }});
        AnalysisResult res2 = new AnalysisResult()
                .setType("he").setValues(new HashMap<String, Integer>(){{
                put("hemax", 32);
                put("coolcat", 32);
                }});

        Results res3 = new Results().setAnalysisResults(Stream.of(res1, res2).collect(Collectors.toList())).setAnnotationId(1l);
        annotationGroupResults = new AnnotationGroupResults().setAnalysisId("1").setAnnotations(new ArrayList<Results>() {{ add(res3); }});
    }

    private AnnotationGroupResults createAnnotationGroupResultsForTests() {
        AnalysisResult res1 = new AnalysisResult().setType("he")
                .setValues(new HashMap<String, Integer>(){{
                    put("hemax", 32);
                    put("coolcat", 32);
                }});
        AnalysisResult res2 = new AnalysisResult()
                .setType("he")
                .setValues(new HashMap<String, Integer>(){{
                    put("hemax", 32);
                    put("coolcat", 32);
                }});
        Results res3 = new Results().setAnalysisResults(Stream.of(res1, res2).collect(Collectors.toList())).setAnnotationId(1l);
        return new AnnotationGroupResults().setAnalysisId("1ask").setAnnotations(new ArrayList<Results>() {{ add(res3); }});
    }



    @Test(expected=NullPointerException.class)
    public void jsonToAnnotationGroupResultsNoGroupId() {
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
    @Test(expected=RuntimeException.class)
    public void jsonToAnnotationGroupResultsEmptyString() {
        dao.jsonToAnnotationGroupResults("");
    }
    @Test(expected=RuntimeException.class)
    public void jsonToAnnotationGroupResultsResultsNoLength() {
        dao.jsonToAnnotationGroupResults(analysisResultJsonStringResultsNoLength);
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
        res.getAnnotations().get(0).setAnalysisResults(new ArrayList<>());
        dao.validateAnnotationGroupResults(res);
    }

    @Test(expected=IllegalArgumentException.class)
    public void validationTestResultTypeFail() {
        AnnotationGroupResults res = createAnnotationGroupResultsForTests();
        res.getAnnotations().get(0).getAnalysisResults().get(0).setType("");
        dao.validateAnnotationGroupResults(res);
    }

    @Test(expected=IllegalArgumentException.class)
    public void validationTestResultValuesFail() {
        AnnotationGroupResults res = createAnnotationGroupResultsForTests();
        res.getAnnotations().get(0).getAnalysisResults().get(0).setValues(new HashMap<String, Integer>());
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
       dao.createAnnotationGroupResults(createAnnotationGroupResultsForTests());
    }

    @Test
    public void getResultsSuccess() {
        AnnotationGroupResults agr = createAnnotationGroupResultsForTests();
        dao.createAnnotationGroupResults(agr);
        List<AnnotationGroupResults> res = dao.getResults(agr.getAnalysisId());
        System.out.println("Get Results success");
        System.out.println(res.get(0).getAnalysisId());
        assertEquals(res.get(0).getAnalysisId(), agr.getAnalysisId());
    }

    @Test
    public void getResultsIsEmpty() {
        AnnotationGroupResults agr = createAnnotationGroupResultsForTests();
        dao.createAnnotationGroupResults(agr);
        List<AnnotationGroupResults> res = dao.getResults("THIS IS NOT VALID");
        assertEquals(res.size(), 0);
    }

    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
}
