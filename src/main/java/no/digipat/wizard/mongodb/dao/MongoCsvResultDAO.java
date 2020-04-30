package no.digipat.wizard.mongodb.dao;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;

import no.digipat.wizard.models.CsvResult;

/**
 * A Data Access Object (DAO) for CSV results.
 * 
 * @author Jon Wallem Anundsen
 *
 */
public class MongoCsvResultDAO {
    
    private final MongoCollection<Document> collection;
    
    /**
     * Creates a DAO.
     * 
     * @param client the client used to connect to the database
     * @param databaseName the name of the database
     */
    public MongoCsvResultDAO(MongoClient client, String databaseName) {
        this.collection = client.getDatabase(databaseName).getCollection("CsvResults");
    }
    
    /**
     * Inserts a new instance of {@code CsvResults} into the database.
     * 
     * @param csvResult the CSV results
     * 
     * @throws NullPointerException if {@code csvResults}
     * or any of its attributes is {@code null}
     * @throws IllegalStateException if the database already contains
     * a CSV result with the same ID as {@code csvResult}
     */
    public void createCsvResult(CsvResult csvResult)
            throws NullPointerException, IllegalStateException {
        try {
            collection.insertOne(csvResultToDocument(csvResult));
        } catch (MongoWriteException e) {
            if (e.getCode() == 11000) { // Error code 11000 indicates a duplicate key
                throw new IllegalStateException("Duplicate analysis ID", e);
            } else {
                throw e;
            }
        }
    }
    
    private static Document csvResultToDocument(CsvResult csvResult) {
        String analysisId = csvResult.getAnalysisId();
        String data = csvResult.getData();
        if (analysisId == null) {
            throw new NullPointerException("Analysis ID cannot be null");
        }
        if (data == null) {
            throw new NullPointerException("Data cannot be null");
        }
        return new Document()
                .append("_id", analysisId)
                .append("data", data);
    }
    
    /**
     * Gets the CSV result for a given analysis from the database.
     * 
     * @param analysisId the analysis ID
     * 
     * @return the CSV result, or {@code null} if no CSV result with the given ID exists
     */
    public CsvResult getCsvResult(String analysisId) {
        Document document =  collection.find(eq("_id", analysisId)).first();
        if (document == null) {
            return null;
        } else {
            return documentToCsvResult(document);
        }
    }
    
    private static CsvResult documentToCsvResult(Document document) {
        return new CsvResult()
                .setAnalysisId(document.getString("_id"))
                .setData(document.getString("data"));
    }
    
}
