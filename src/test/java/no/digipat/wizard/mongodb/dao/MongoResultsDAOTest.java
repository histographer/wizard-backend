package no.digipat.wizard.mongodb.dao;

import com.mongodb.client.FindIterable;
import no.digipat.wizard.models.AnnotationGroupResults;
import no.digipat.wizard.models.Result;
import no.digipat.wizard.models.Results;
import org.bson.Document;
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
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mongodb.client.model.Filters.eq;

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
        analysisResultJsonString = "{\"groupId\":\"testGroupdId\",\"results\":[{\"annotationId\":\"id\",\"results\":[{\"type\": \"he\",\"values\":{\"hematoxylin\":180,\"eosin\": 224}}]}]}";
        analysisResultJsonStringNoGroupId = "{\"results\":[{\"annotationId\":\"id\",\"results\":[{\"type\": \"he\",\"values\":{\"hematoxylin\":180,\"eosin\": 224}}]}]}";
        analysisResultJsonStringNoResults = "{\"groupId\":\"testGroupdId\"";
        analysisResultJsonStringResultsNoLength = "{\"groupId\":\"testGroupdId\",\"results\":[]}]}";
        Result res1 = new Result().setType("he")
                .setValues(new HashMap<String, Integer>(){{
                put("hemax", 32);
                put("coolcat", 32);
                }});
        Result res2 = new Result()
                .setType("he").setValues(new HashMap<String, Integer>(){{
                put("hemax", 32);
                put("coolcat", 32);
                }});

        Results res3 = new Results().setResults(Stream.of(res1, res2).collect(Collectors.toList())).setAnnotationId("1");
        annotationGroupResults = new AnnotationGroupResults().setGroupId("1").setResults(new ArrayList<Results>() {{ add(res3); }});

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
    public void addAnnotationGroupResults() {
        dao.createAnnotationGroupResults(annotationGroupResults);
    }

    // TODO make test for getAnnotationGroupResults and check inserts

    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }


}
