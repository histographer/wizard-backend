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
import no.digipat.wizard.models.CsvResult;
import no.digipat.wizard.mongodb.DatabaseUnitTests;

@RunWith(JUnitParamsRunner.class)
public class MongoCsvResultDAOTest {
    
    private static final String hexId = "abcdef0123456789abcdef12";
    private static MongoClient client;
    private static String databaseName;
    private MongoCsvResultDAO dao;
    
    @BeforeClass
    public static void setUpClass() {
        client = DatabaseUnitTests.getMongoClient();
        databaseName = DatabaseUnitTests.getDatabaseName();
    }
    
    @Before
    public void setUp() {
        dao = new MongoCsvResultDAO(client, databaseName);
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
    @Test(expected = NullPointerException.class)
    @Parameters(method = "getNullpointerResults")
    public void testNullPointerException(CsvResult csvResult) {
        dao.createCsvResult(csvResult);
    }
    
    private static CsvResult[] getNullpointerResults() {
        return new CsvResult[] {
            null,
            new CsvResult().setAnalysisId("abc"),
            new CsvResult().setData("datadatadata")
        };
    }
    
    @Test(expected = IllegalStateException.class)
    public void testCreateDuplicateCsvResult() throws Exception {
        dao.createCsvResult(new CsvResult().setAnalysisId(hexId).setData("blah"));
        dao.createCsvResult(new CsvResult().setAnalysisId(hexId).setData("bleh"));
    }
    
    @Test
    public void testCreateCsvResult() throws Exception {
        CsvResult csvResult = new CsvResult()
                .setAnalysisId(hexId)
                .setData("some data");
        
        dao.createCsvResult(csvResult);
        CsvResult retrievedResult = dao.getCsvResult(csvResult.getAnalysisId());
        
        assertEquals(csvResult, retrievedResult);
    }
    
    @Test
    public void testGetNonexistentCsvResult() throws Exception {
        dao.createCsvResult(new CsvResult()
            .setAnalysisId("0123456789abcdef01234567") // Different ID from the one we use later
            .setData("blah blah")
        );
        
        CsvResult result = dao.getCsvResult("abcdef0123456789abcdef12");
        
        assertNull(result);
    }
    
}
