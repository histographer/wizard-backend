package no.digipat.wizard.mongodb.dao;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mongodb.MongoClient;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import no.digipat.wizard.models.AnalysisStatus;
import no.digipat.wizard.models.AnalysisStatus.Status;
import no.digipat.wizard.mongodb.DatabaseUnitTests;

@RunWith(JUnitParamsRunner.class)
public class MongoAnalysisStatusDAOTest {
    
    private static final String hexId = "0123456789abcdef01234567"; // 24 hexadecimal characters
    private static MongoClient client;
    private static String databaseName;
    private MongoAnalysisStatusDAO dao;
    
    @BeforeClass
    public static void setUpClass() {
        client = DatabaseUnitTests.getMongoClient();
        databaseName = DatabaseUnitTests.getDatabaseName();
    }
    
    @Before
    public void setUp() {
        dao = new MongoAnalysisStatusDAO(client, databaseName);
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
    private static AnalysisStatus createAnalysisStatusWithNonNullFields() {
        return new AnalysisStatus()
                .setAnalysisId(hexId)
                .setAnnotationGroupId(hexId)
                .setStatus(Status.PENDING);
    }
    
    @Test(expected=NullPointerException.class)
    public void testCreateAnalysisWithNullStatus() {
        dao.createAnalysisStatus(createAnalysisStatusWithNonNullFields().setStatus(null));
    }
    
    @Test(expected=NullPointerException.class)
    public void testCreateAnalysisWithNullGroupId() {
        dao.createAnalysisStatus(createAnalysisStatusWithNonNullFields().setAnnotationGroupId(null));
    }
    
    @Test(expected=IllegalArgumentException.class)
    @Parameters({
        "abc",
        "oooooooooooooooooooooooo"
    })
    public void testCreateAnalysisWithInvalidId(String id) {
        dao.createAnalysisStatus(createAnalysisStatusWithNonNullFields().setAnalysisId(id));
    }
    
    @Test(expected=IllegalStateException.class)
    public void testCreateAnalysisWithDuplicateId() {
        AnalysisStatus analysisStatus = createAnalysisStatusWithNonNullFields();
        dao.createAnalysisStatus(analysisStatus);
        dao.createAnalysisStatus(analysisStatus);
    }
    
    @Test
    public void testCreateAnalysisWithNullId() {
        AnalysisStatus analysis = new AnalysisStatus()
                .setAnnotationGroupId(hexId)
                .setStatus(Status.PENDING);
        
        String id = dao.createAnalysisStatus(analysis);
        AnalysisStatus retrievedAnalysis = dao.getAnalysisStatus(id);
        
        assertNotNull(id);
        analysis.setAnalysisId(id); // Set ID so we can use equals method
        assertEquals(analysis, retrievedAnalysis);
    }
    
    @Test
    public void testCreateAnalysisWithProvidedId() {
        AnalysisStatus analysis = new AnalysisStatus()
                .setAnalysisId(hexId)
                .setAnnotationGroupId("abcdef0123456789abcdef12")
                .setStatus(Status.SUCCESS);
        
        String id = dao.createAnalysisStatus(analysis);
        AnalysisStatus retrievedAnalysis = dao.getAnalysisStatus(id);
        
        assertEquals(analysis.getAnalysisId(), id);
        assertEquals(analysis, retrievedAnalysis);
    }
    
    @Test(expected=IllegalArgumentException.class)
    @Parameters({
        "abc",
        "oooooooooooooooooooooooo"
    })
    public void testGetAnalysisWithInvalidId(String id) {
        dao.getAnalysisStatus(id);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testGetAnalysisWithNullId() {
        dao.getAnalysisStatus(null);
    }
    
    @Test
    public void testGetNonexistentAnalysis() {
        assertNull(dao.getAnalysisStatus(hexId));
    }
    
    @Test(expected=NullPointerException.class)
    public void testUpdateNullStatus() {
        dao.updateStatus(hexId, null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    @Parameters({
        "abc",
        "oooooooooooooooooooooooo"
    })
    public void testUpdateInvalidId(String id) {
        dao.updateStatus(id, Status.SUCCESS);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testUpdateNullId() {
        dao.updateStatus(null, Status.SUCCESS);
    }
    
    @Test
    public void testUpdateStatusOfNonexistentAnalysis() {
        assertFalse(dao.updateStatus(hexId, Status.SUCCESS));
    }
    
    @Test
    public void testUpdateStatus() {
        AnalysisStatus analysis = new AnalysisStatus()
                .setAnalysisId(hexId)
                .setAnnotationGroupId(hexId)
                .setStatus(Status.PENDING);
        dao.createAnalysisStatus(analysis);
        
        boolean updated = dao.updateStatus(analysis.getAnalysisId(), Status.FAILURE);
        
        assertTrue(updated);
        assertEquals(Status.FAILURE, dao.getAnalysisStatus(analysis.getAnalysisId()).getStatus());
    }
        
}
