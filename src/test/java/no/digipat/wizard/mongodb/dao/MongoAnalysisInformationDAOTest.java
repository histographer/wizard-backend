package no.digipat.wizard.mongodb.dao;

import static org.junit.Assert.*;

import java.util.Arrays;
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
import no.digipat.wizard.mongodb.DatabaseUnitTests;

@RunWith(JUnitParamsRunner.class)
public class MongoAnalysisInformationDAOTest {
    
    private static final String hexId = "0123456789abcdef01234567"; // 24 hexadecimal characters
    private static MongoClient client;
    private static String databaseName;
    private MongoAnalysisInformationDAO dao;
    
    @BeforeClass
    public static void setUpClass() {
        client = DatabaseUnitTests.getMongoClient();
        databaseName = DatabaseUnitTests.getDatabaseName();
    }
    
    @Before
    public void setUp() {
        dao = new MongoAnalysisInformationDAO(client, databaseName);
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
        dao.createAnalysisInformation(createAnalysisInfoWithNonNullFields().setStatus(null));
    }
    
    @Test(expected = NullPointerException.class)
    public void testCreateAnalysisWithNullGroupId() {
        dao.createAnalysisInformation(
                createAnalysisInfoWithNonNullFields().setAnnotationGroupId(null)
        );
    }
    
    @Test(expected = IllegalArgumentException.class)
    @Parameters({
        "abc",
        "oooooooooooooooooooooooo"
    })
    public void testCreateAnalysisWithInvalidId(String id) {
        dao.createAnalysisInformation(createAnalysisInfoWithNonNullFields().setAnalysisId(id));
    }
    
    @Test(expected = IllegalStateException.class)
    public void testCreateAnalysisWithDuplicateId() {
        AnalysisInformation analysisInformation = createAnalysisInfoWithNonNullFields();
        dao.createAnalysisInformation(analysisInformation);
        dao.createAnalysisInformation(analysisInformation);
    }
    
    @Test
    public void testCreateAnalysisWithNullId() {
        AnalysisInformation analysis = new AnalysisInformation()
                .setAnnotationGroupId(hexId)
                .setStatus(Status.PENDING);
        
        String id = dao.createAnalysisInformation(analysis);
        AnalysisInformation retrievedAnalysis = dao.getAnalysisInformation(id);
        
        assertNotNull(id);
        analysis.setAnalysisId(id); // Set ID so we can use equals method
        assertEquals(analysis, retrievedAnalysis);
    }
    
    @Test
    public void testCreateAnalysisWithProvidedId() {
        AnalysisInformation analysis = new AnalysisInformation()
                .setAnalysisId(hexId)
                .setAnnotationGroupId("abcdef0123456789abcdef12")
                .setStatus(Status.SUCCESS);
        
        String id = dao.createAnalysisInformation(analysis);
        AnalysisInformation retrievedAnalysis = dao.getAnalysisInformation(id);
        
        assertEquals(analysis.getAnalysisId(), id);
        assertEquals(analysis, retrievedAnalysis);
    }
    
    @Test(expected = IllegalArgumentException.class)
    @Parameters({
        "abc",
        "oooooooooooooooooooooooo"
    })
    public void testGetAnalysisWithInvalidId(String id) {
        dao.getAnalysisInformation(id);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetAnalysisWithNullId() {
        dao.getAnalysisInformation(null);
    }
    
    @Test
    public void testGetNonexistentAnalysis() {
        assertNull(dao.getAnalysisInformation(hexId));
    }
    
    @Test(expected = NullPointerException.class)
    public void testUpdateNullStatus() {
        dao.updateStatus(hexId, null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "getInvalidIds")
    public void testUpdateInvalidId(String id) {
        dao.updateStatus(id, Status.SUCCESS);
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
        assertFalse(dao.updateStatus(hexId, Status.SUCCESS));
    }
    
    @Test
    public void testUpdateStatus() {
        AnalysisInformation analysis = new AnalysisInformation()
                .setAnalysisId(hexId)
                .setAnnotationGroupId(hexId)
                .setStatus(Status.PENDING);
        dao.createAnalysisInformation(analysis);
        
        boolean updated = dao.updateStatus(analysis.getAnalysisId(), Status.FAILURE);
        
        assertTrue(updated);
        assertEquals(
                Status.FAILURE,
                dao.getAnalysisInformation(analysis.getAnalysisId()).getStatus()
        );
    }
    
    @Test
    public void testGetAnalysesForGroup() throws Exception {
        AnalysisInformation info1 = new AnalysisInformation()
                .setAnnotationGroupId("abcdef0123456789abcdef12")
                .setStatus(Status.PENDING);
        String id1 = dao.createAnalysisInformation(info1);
        AnalysisInformation info2 = new AnalysisInformation()
                .setAnnotationGroupId("abcdef0123456789abcdef12")
                .setStatus(Status.SUCCESS);
        String id2 = dao.createAnalysisInformation(info2);
        AnalysisInformation info3 = new AnalysisInformation()
                .setAnnotationGroupId("0123456789abcdef01234567")
                .setStatus(Status.FAILURE);
        dao.createAnalysisInformation(info3);
        
        List<AnalysisInformation> analyses = dao
                .getAnalysisInformationForAnnotationGroup("abcdef0123456789abcdef12");
        
        assertEquals(
                Arrays.asList(info1.setAnalysisId(id1), info2.setAnalysisId(id2)),
                analyses
        );
    }
    
    @Test
    public void testGetAnalysesForNonexistentGroup() throws Exception {
        List<AnalysisInformation> analyses = dao.getAnalysisInformationForAnnotationGroup(hexId);
        
        assertEquals("List is not empty.", 0, analyses.size());
    }
    
}
