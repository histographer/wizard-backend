package no.digipat.wizard.mongodb;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import no.digipat.wizard.mongodb.dao.MongoResultsDAOTest;
import no.digipat.wizard.mongodb.dao.MongoResultsDAOTest;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.mongodb.MongoClient;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import no.digipat.wizard.mongodb.dao.MongoAnalysisInformationDAOTest;
import no.digipat.wizard.mongodb.dao.MongoAnnotationGroupDAOTest;
import no.digipat.wizard.mongodb.dao.MongoCsvResultDAOTest;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * A suite of unit tests that require database connectivity.
 * Test cases in this suite should clear the contents of the
 * test database once all their test methods have been run.
 * 
 * @author Jon Wallem Anundsen
 *
 */
@RunWith(Suite.class)
@SuiteClasses({
    MongoAnnotationGroupDAOTest.class,
    MongoAnalysisInformationDAOTest.class,
    MongoResultsDAOTest.class,
    MongoCsvResultDAOTest.class
})
public class DatabaseUnitTests {
    
    private static MongoClient client;
    private static MongoServer server;
    private static String databaseName = "test";
    
    @BeforeClass
    public static void setUpClass() {
        server = new MongoServer(new MemoryBackend());
        server.bind("localhost", 27017);

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        client = new MongoClient(new ServerAddress("localhost", 27017), MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());
    }
    
    /**
     * Gets a client for accessing the test database.
     * 
     * @return a client for the test database
     */
    public static MongoClient getMongoClient() {
        return client;
    }
    
    /**
     * Gets the name of the test database.
     * 
     * @return the name of the test database
     */
    public static String getDatabaseName() {
        return databaseName;
    }
    
    @AfterClass
    public static void tearDownClass() {
        client.close();
        server.shutdown();
    }
    
}
