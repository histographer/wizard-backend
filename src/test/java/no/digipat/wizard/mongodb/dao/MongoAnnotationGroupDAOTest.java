package no.digipat.wizard.mongodb.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
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
import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.mongodb.DatabaseUnitTests;

@RunWith(JUnitParamsRunner.class)
public class MongoAnnotationGroupDAOTest {
    
    private static final String hexId = "0123456789abcdef01234567"; // 24 hexadecimal characters
    private static MongoClient client;
    private static String databaseName;
    private MongoAnnotationGroupDAO dao;
    
    @BeforeClass
    public static void setUpClass() {
        client = DatabaseUnitTests.getMongoClient();
        databaseName = DatabaseUnitTests.getDatabaseName();
    }
    
    @Before
    public void setUp() {
        dao = new MongoAnnotationGroupDAO(client, databaseName);
    }
    
    @Test(expected=NullPointerException.class)
    public void testCreateNullAnnotationGroup() {
        dao.createAnnotationGroup(null);
    }
    
    @Test(expected=NullPointerException.class)
    public void testCreateAnnotationGroupWithNullAnnotationIds() {
        dao.createAnnotationGroup(new AnnotationGroup().setGroupId(hexId));
    }
    
    @Test(expected=IllegalArgumentException.class)
    @Parameters({
        "abc", // Too short
        "oooooooooooooooooooooooo" // 24 non-hexadecimal characters
    })
    public void testCreateAnnotationWithInvalidId(String id) {
        dao.createAnnotationGroup(new AnnotationGroup().setGroupId(id).setAnnotationIds(list(1)));
    }
    
    @Test(expected=IllegalStateException.class)
    public void testCreateAnnotationGroupWithDuplicateId() {
        dao.createAnnotationGroup(new AnnotationGroup().setGroupId(hexId)
                .setAnnotationIds(list(1, 2)));
        dao.createAnnotationGroup(new AnnotationGroup().setGroupId(hexId)
                .setAnnotationIds(list(3, 4)));
    }
    
    @Test
    public void testCreateAnnotationGroupWithSuppliedId() {
        AnnotationGroup group = new AnnotationGroup().setAnnotationIds(list(1, 2))
                .setGroupId(hexId).setCreationDate(new Date());
        String id = dao.createAnnotationGroup(group);
        AnnotationGroup createdGroup = dao.getAnnotationGroup(group.getGroupId());
        
        assertEquals(group.getGroupId(), id);
        assertEquals(group.getGroupId(), createdGroup.getGroupId());
        assertEquals(group.getAnnotationIds(), createdGroup.getAnnotationIds());
        assertEquals(group.getCreationDate(), createdGroup.getCreationDate());
    }
    
    @Test
    public void testCreateAnnotationGroupWithNullId() {
        AnnotationGroup group = new AnnotationGroup().setAnnotationIds(list(1, 2))
                .setCreationDate(new Date());
        String id = dao.createAnnotationGroup(group);
        AnnotationGroup createdGroup = dao.getAnnotationGroup(id);
        
        assertNotNull(id);
        assertEquals(id, createdGroup.getGroupId());
        assertEquals(group.getAnnotationIds(), createdGroup.getAnnotationIds());
        assertEquals(group.getCreationDate(), createdGroup.getCreationDate());
    }
    
    @Test
    public void testGetNonexistentAnnotationGroup() {
        AnnotationGroup group = dao.getAnnotationGroup(hexId);
        
        assertNull(group);
    }
    
    @After
    public void tearDown() {
        client.getDatabase(databaseName).drop();
    }
    
    private static List<Long> list(long... values) {
        // Utility method to avoid typing so much stuff
        List<Long> theList = new ArrayList<>();
        for (long value : values) {
            theList.add(value);
        }
        return theList;
    }
    
}
