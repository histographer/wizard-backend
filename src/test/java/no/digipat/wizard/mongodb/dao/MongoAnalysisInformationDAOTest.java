package no.digipat.wizard.mongodb.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mongodb.MongoClient;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import no.digipat.wizard.models.AnalysisInformation;
import no.digipat.wizard.models.AnalysisInformation.Status;
import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.mongodb.DatabaseUnitTests;

@RunWith(JUnitParamsRunner.class)
public class MongoAnalysisInformationDAOTest {
    
    private static final String hexId = "0123456789abcdef01234567"; // 24 hexadecimal characters
    private static MongoClient client;
    private static String databaseName;
    private MongoAnalysisInformationDAO infoDao;
    private MongoAnnotationGroupDAO groupDao;
    private static final AnnotationGroup group = new AnnotationGroup()
            .setAnnotationIds(new ArrayList<Long>())
            .setCreationDate(new Date())
            .setGroupId("aaaaaaaaaaaaaaaaaaaaaaaa")
            .setName("test group")
            .setProjectId(20L);
    
    @BeforeClass
    public static void setUpClass() {
        client = DatabaseUnitTests.getMongoClient();
        databaseName = DatabaseUnitTests.getDatabaseName();
    }
    
    @Before
    public void setUp() {
        infoDao = new MongoAnalysisInformationDAO(client, databaseName);
        groupDao = new MongoAnnotationGroupDAO(client, databaseName);
        groupDao.createAnnotationGroup(group);
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
    private static AnalysisInformation createAnalysisInfoWithNonNullFields() {
        return new AnalysisInformation()
                .setAnalysisId(hexId)
                .setAnnotationGroupId(hexId)
                .setStatus(Status.PENDING);
    }
    
    @Test(expected = NullPointerException.class)
    public void testCreateAnalysisWithNullStatus() {
        infoDao.createAnalysisInformation(createAnalysisInfoWithNonNullFields().setStatus(null));
    }
    
    @Test(expected = NullPointerException.class)
    public void testCreateAnalysisWithNullGroupId() {
        infoDao.createAnalysisInformation(
                createAnalysisInfoWithNonNullFields().setAnnotationGroupId(null)
        );
    }
    
    @Test(expected = IllegalArgumentException.class)
    @Parameters({
        "abc",
        "oooooooooooooooooooooooo"
    })
    public void testCreateAnalysisWithInvalidId(String id) {
        infoDao.createAnalysisInformation(createAnalysisInfoWithNonNullFields().setAnalysisId(id));
    }
    
    @Test(expected = IllegalStateException.class)
    public void testCreateAnalysisWithDuplicateId() {
        AnalysisInformation analysisInformation = createAnalysisInfoWithNonNullFields();
        infoDao.createAnalysisInformation(analysisInformation);
        infoDao.createAnalysisInformation(analysisInformation);
    }
    
    @Test
    public void testCreateAnalysisWithNullId() {
        AnalysisInformation analysis = new AnalysisInformation()
                .setAnnotationGroupId(group.getGroupId())
                .setStatus(Status.PENDING)
                .setGroupName("this group name should be ignored");
        
        String id = infoDao.createAnalysisInformation(analysis);
        AnalysisInformation retrievedAnalysis = infoDao.getAnalysisInformation(id);
        
        assertNotNull(id);
        // Set ID and group name so we can use equals method:
        analysis.setAnalysisId(id).setGroupName(group.getName());
        assertEquals(analysis, retrievedAnalysis);
    }
    
    @Test
    public void testCreateAnalysisWithProvidedId() {
        AnalysisInformation analysis = new AnalysisInformation()
                .setAnalysisId(hexId)
                .setAnnotationGroupId(group.getGroupId())
                .setStatus(Status.SUCCESS)
                .setGroupName("this group name should be ignored");
        
        String id = infoDao.createAnalysisInformation(analysis);
        AnalysisInformation retrievedAnalysis = infoDao.getAnalysisInformation(id);
        
        // Set group name so we can use equals method:
        analysis.setGroupName(group.getName());
        assertEquals(analysis.getAnalysisId(), id);
        assertEquals(analysis, retrievedAnalysis);
    }
    
    @Test(expected = IllegalArgumentException.class)
    @Parameters({
        "abc",
        "oooooooooooooooooooooooo"
    })
    public void testGetAnalysisWithInvalidId(String id) {
        infoDao.getAnalysisInformation(id);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetAnalysisWithNullId() {
        infoDao.getAnalysisInformation(null);
    }
    
    @Test
    public void testGetNonexistentAnalysis() {
        assertNull(infoDao.getAnalysisInformation(hexId));
    }
    
    @Test(expected = NullPointerException.class)
    public void testUpdateNullStatus() {
        infoDao.updateStatus(hexId, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "getInvalidIds")
    public void testUpdateInvalidId(String id) {
        infoDao.updateStatus(id, Status.SUCCESS);
    }
    
    private static String[] getInvalidIds() {
        return new String[] {
            "abc",
            "oooooooooooooooooooooooo",
            null
        };
    }
    
    @Test
    public void testUpdateStatusOfNonexistentAnalysis() {
        assertFalse(infoDao.updateStatus(hexId, Status.SUCCESS));
    }
    
    @Test
    public void testUpdateStatus() {
        AnalysisInformation analysis = new AnalysisInformation()
                .setAnalysisId(hexId)
                .setAnnotationGroupId(hexId)
                .setStatus(Status.PENDING);
        infoDao.createAnalysisInformation(analysis);
        
        boolean updated = infoDao.updateStatus(analysis.getAnalysisId(), Status.FAILURE);
        
        assertTrue(updated);
        assertEquals(
                Status.FAILURE,
                infoDao.getAnalysisInformation(analysis.getAnalysisId()).getStatus()
        );
    }
    
    @Test
    public void testGetAnalysesForGroup() throws Exception {
        AnalysisInformation info1 = new AnalysisInformation()
                .setAnnotationGroupId(group.getGroupId())
                .setStatus(Status.PENDING)
                .setGroupName("this group name should be ignored");
        String id1 = infoDao.createAnalysisInformation(info1);
        AnalysisInformation info2 = new AnalysisInformation()
                .setAnnotationGroupId(group.getGroupId())
                .setStatus(Status.SUCCESS)
                .setGroupName("this group name should be ignored");
        String id2 = infoDao.createAnalysisInformation(info2);
        AnalysisInformation info3 = new AnalysisInformation()
                .setAnnotationGroupId("0123456789abcdef01234567")
                .setStatus(Status.FAILURE);
        infoDao.createAnalysisInformation(info3);
        
        List<AnalysisInformation> analyses = infoDao
                .getAnalysisInformationForAnnotationGroup(group.getGroupId());
        
        assertEquals(
                Arrays.asList(
                        info1.setAnalysisId(id1).setGroupName(group.getName()),
                        info2.setAnalysisId(id2).setGroupName(group.getName())
                ),
                analyses
        );
    }
    
    @Test
    public void testGetAnalysesForNonexistentGroup() throws Exception {
        List<AnalysisInformation> analyses = infoDao
                .getAnalysisInformationForAnnotationGroup(hexId);
        
        assertEquals("List is not empty.", 0, analyses.size());
    }
    
    @Test
    public void testGroupNameWhenGettingSingleAnalysisWithNonexistentGroup() throws Exception {
        AnalysisInformation info = new AnalysisInformation()
                .setAnalysisId(hexId)
                .setAnnotationGroupId("aaa")
                .setStatus(Status.SUCCESS)
                .setGroupName("this group name should be ignored");
        
        infoDao.createAnalysisInformation(info);
        AnalysisInformation retrievedInfo = infoDao.getAnalysisInformation(info.getAnalysisId());
        
        assertNull("Group name should be null when the group does not exist",
                retrievedInfo.getGroupName());
        assertEquals(info.setGroupName(null), retrievedInfo);
    }
    
    @Test
    public void testGroupNameWhenGettingAllAnalysesWithNonexistentGroup() throws Exception {
        AnalysisInformation info = new AnalysisInformation()
                .setAnalysisId(hexId)
                .setAnnotationGroupId("aaa")
                .setStatus(Status.SUCCESS)
                .setGroupName("this group name should be ignored");
        
        infoDao.createAnalysisInformation(info);
        List<AnalysisInformation> infoList = infoDao
                .getAnalysisInformationForAnnotationGroup("aaa");
        
        assertNull("Group name should be null when the group does not exist",
                infoList.get(0).getGroupName());
        assertEquals(Arrays.asList(info.setGroupName(null)), infoList);
    }
    
}
