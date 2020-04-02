package no.digipat.wizard.servlets;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import static java.util.Comparator.comparing;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.mongodb.MongoClient;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.mongodb.dao.MongoAnnotationGroupDAO;

@RunWith(JUnitParamsRunner.class)
public class AnnotationGroupServletTest {
    
    private static URL baseUrl;
    private static String databaseName;
    private static MongoClient client;
    private MongoAnnotationGroupDAO dao;
    private WebConversation conversation;
    
    @BeforeClass
    public static void setUpClass() {
        baseUrl = IntegrationTests.getBaseUrl();
        databaseName = IntegrationTests.getDatabaseName();
        client = IntegrationTests.getMongoClient();
    }
    
    @Before
    public void setUp() {
        dao = new MongoAnnotationGroupDAO(client, databaseName);
        conversation = new WebConversation();
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
    private static PostMethodWebRequest createPostRequest(String path, String messageBody, String contentType) throws Exception {
        return new PostMethodWebRequest(new URL(baseUrl, path).toString(),
                new ByteArrayInputStream(messageBody.getBytes("UTF-8")), contentType);
    }
    
    @Test
    @Parameters(method="getInvalidGroupCreationRequestBodies")
    public void testStatusCode400OnGroupCreation(String messageBody) throws Exception {
        WebRequest request = createPostRequest("annotationGroup", messageBody, "application/json");
        WebResponse response = conversation.getResponse(request);
        
        assertEquals("Testing with message body: " + messageBody + ".", 400, response.getResponseCode());
    }
    
    private static String[][] getInvalidGroupCreationRequestBodies() {
        return new String[][] {
                {"this is not JSON"},
                {"{}"},
                // Invalid or missing array of annotations:
                {"{\"name\": \"group name\", \"projectId\": 1}"},
                {"{\"annotations\": \"not an array\", \"name\": \"group name\", \"projectId\": 1}"},
                {"{\"annotations\": [\"not a number\"], \"name\": \"group name\", \"projectId\": 1}"},
                {"{\"annotations\": null, \"name\": \"group name\"}, \"projectId\": 1"},
                {"{\"annotations\": [null], \"name\": \"group name\"}, \"projectId\": 1"},
                // Invalid or missing group name:
                {"{\"annotations\": [1]}, \"projectId\": 1"},
                {"{\"annotations\": [1], \"name\": null, \"projectId\": 1}"},
                {"{\"annotations\": [1], \"name\": 1, \"projectId\": 1}"},
                // Invalid or missing project name:
                {"{\"annotations\": [1], \"name\": \"group name\"}"},
                {"{\"annotations\": [1], \"name\": \"group name\", \"projectId\": null}"},
                {"{\"annotations\": [1], \"name\": \"group name\", \"projectId\": \"this is a string\"}"}
        };
    }
    
    @Test
    public void testCreateAnnotationGroup() throws Exception {
        JSONObject requestJson = new JSONObject();
        List<Long> annotationIds = Arrays.asList(new Long[] {42L, 1337L, Long.MAX_VALUE});
        requestJson.put("annotations", annotationIds);
        requestJson.put("name", "foo");
        requestJson.put("projectId", 20);
        
        WebRequest request = createPostRequest("annotationGroup", requestJson.toString(), "application/json");
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(200, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
        JSONObject responseJson = new JSONObject(response.getText());
        String groupId = responseJson.getString("groupId");
        assertNotNull(groupId);
        AnnotationGroup group = dao.getAnnotationGroup(groupId);
        assertNotNull(group);
        assertEquals(annotationIds, group.getAnnotationIds());
        Date now = new Date();
        long nowMilliseconds = now.getTime();
        long earliestMilliseconds = nowMilliseconds - 60*1000; // One minute ago
        long latestMilliseconds = nowMilliseconds + 60*1000; // One minute into the future
        long creationMilliseconds = group.getCreationDate().getTime();
        // We can't really expect the creation time to be an exact value,
        // but it certainly shouldn't be off by more than a minute
        assertTrue("Creation time is too early", creationMilliseconds >= earliestMilliseconds);
        assertTrue("Creation time is too late", creationMilliseconds <= latestMilliseconds);
        assertEquals("foo", group.getName());
        assertEquals((Long) 20L, group.getProjectId());
    }
    
    @Test
    public void testGetAnnotationGroups() throws Exception {
        AnnotationGroup group1 = new AnnotationGroup()
                .setGroupId("aaaaaaaaaaaaaaaaaaaaaaaa")
                .setAnnotationIds(Arrays.asList(1L, 2L))
                .setCreationDate(new Date())
                .setName("group 1")
                .setProjectId(20L);
        AnnotationGroup group2 = new AnnotationGroup()
                .setGroupId("bbbbbbbbbbbbbbbbbbbbbbbb")
                .setAnnotationIds(Arrays.asList(3L))
                .setCreationDate(new Date(0))
                .setName("group 2")
                .setProjectId(20L);
        AnnotationGroup group3 = new AnnotationGroup()
                .setGroupId("cccccccccccccccccccccccc")
                .setAnnotationIds(Arrays.asList(4L))
                .setCreationDate(new Date(-100000000))
                .setName("group 3")
                .setProjectId(30L);
        dao.createAnnotationGroup(group1);
        dao.createAnnotationGroup(group2);
        dao.createAnnotationGroup(group3);
        
        WebRequest request = new GetMethodWebRequest(baseUrl, "annotationGroup?projectId=20");
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(200, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
        JSONObject jsonObject = new JSONObject(response.getText());
        JSONArray array = jsonObject.getJSONArray("groups");
        List<Object> list = array.toList();
        assertEquals(2, list.size());
        Collections.sort(list, comparing(obj -> (String) ((Map) obj).get("id")));
        Map<String, Object> map1 = (Map<String, Object>) list.get(0);
        Map<String, Object> map2 = (Map<String, Object>) list.get(1);
        assertEquals(group1.getGroupId(), map1.get("id"));
        assertEquals(group1.getName(), map1.get("name"));
        assertEquals(group2.getGroupId(), map2.get("id"));
        assertEquals(group2.getName(), map2.get("name"));
    }
    
    @Test
    @Parameters({
        "annotationGroup",
        "annotationGroup?projectId=",
        "annotationGroup?projectId=notANumber"
    })
    public void testStatusCode400OnGetAnnotationGroups(String path) throws Exception {
        WebRequest request = new GetMethodWebRequest(baseUrl, path);
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(400, response.getResponseCode());
    }
    
    @Test
    public void testUnicodeCharactersOnCreation() throws Exception {
        JSONObject requestJson = new JSONObject();
        List<Long> annotationIds = Arrays.asList(new Long[] {42L, 1337L, Long.MAX_VALUE});
        requestJson.put("annotations", annotationIds);
        String groupName = "ÆØÅæøåαβγ";
        requestJson.put("name", groupName);
        requestJson.put("projectId", 20);
        
        WebRequest request = createPostRequest("annotationGroup", requestJson.toString(), "application/json");
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(200, response.getResponseCode());
        JSONObject responseJson = new JSONObject(response.getText());
        String groupId = responseJson.getString("groupId");
        AnnotationGroup group = dao.getAnnotationGroup(groupId);
        assertEquals(groupName, group.getName());
    }
    
    @Test
    public void testUnicodeCharactersOnRetrieval() throws Exception {
        AnnotationGroup group = new AnnotationGroup()
                .setCreationDate(new Date())
                .setAnnotationIds(new ArrayList<Long>())
                .setProjectId(20L)
                .setName("ÆØÅæøåαβγ");
        dao.createAnnotationGroup(group);
        
        WebRequest request = new GetMethodWebRequest(baseUrl, "annotationGroup?projectId=" + 20);
        WebResponse response = conversation.getResponse(request);
        System.out.println(response.getContentType());
        System.out.println(response.getCharacterSet());
        System.out.println(response.getHeaderField("content-type"));
        System.out.println(StandardCharsets.UTF_8.name());
        
        assertEquals(200, response.getResponseCode());
        JSONObject json = new JSONObject(response.getText());
        System.out.println(response.getText());
        JSONArray array = json.getJSONArray("groups");
        assertEquals(group.getName(), ((JSONObject) array.get(0)).get("name"));
    }
    
}
