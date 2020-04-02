package no.digipat.wizard.mongodb.dao;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import no.digipat.wizard.models.AnalysisInformation;
import no.digipat.wizard.models.AnalysisInformation.Status;

/**
 * A Data Access Object (DAO) for analysis info.
 * 
 * @author Jon Wallem Anundsen
 *
 */
public class MongoAnalysisInformationDAO {
    
    private final MongoCollection<Document> collection;
    
    public MongoAnalysisInformationDAO(MongoClient client, String databaseName) {
        this.collection = client.getDatabase(databaseName).getCollection("AnalysisInformation");
    }
    
    /**
     * Inserts a new analysis information object into the database. If the ID of the
     * analysis is {@code null}, an ID will be autogenerated.
     * 
     * @param analysisInformation the analysis information
     * 
     * @return the ID of the inserted analysis
     * 
     * @throws NullPointerException if {@code analysisInformation} or any of its attributes
     * is {@code null}
     * @throws IllegalArgumentException if the provided analysis ID is not either
     * {@code null} or a 24-character hexadecimal string
     * @throws IllegalStateException if an analysis with the given ID already exists
     * 
     */
    public String createAnalysisInformation(AnalysisInformation analysisInformation)
            throws NullPointerException, IllegalArgumentException, IllegalStateException {
        Document document = analysisInformationToDocument(analysisInformation);
        try {
            collection.insertOne(document);
        } catch (MongoWriteException e) {
            if (e.getCode() == 11000) { // Error code 11000 indicates a duplicated key
                throw new IllegalStateException("Duplicate analysis ID", e);
            } else {
                throw e;
            }
        }
        return document.getObjectId("_id").toHexString();
    }
    
    private static Document analysisInformationToDocument(AnalysisInformation analysisInformation) {
        Document document = new Document();
        String id = analysisInformation.getAnalysisId();
        if (id != null) {
            document.append("_id", new ObjectId(analysisInformation.getAnalysisId()));
        }
        String groupId = analysisInformation.getAnnotationGroupId();
        if (groupId == null) {
            throw new NullPointerException("Annotation group ID cannot be null");
        }
        document.append("annotationGroupId", groupId)
                .append("status", analysisInformation.getStatus().name());
        return document;
    }
    
    /**
     * Gets information about an analysis from the database.
     * 
     * @param analysisId the ID of the analysis
     * 
     * @return the analysis information, or {@code null} if it does not exist
     * 
     * @throws IllegalArgumentException if {@code analysisId} is not a
     * 24-character hexadecimal string
     */
    public AnalysisInformation getAnalysisInformation(String analysisId) throws IllegalArgumentException {
        Document document = collection.find(eq("_id", new ObjectId(analysisId))).first();
        if (document == null) {
            return null;
        } else {
            return documentToAnalysisInformation(document);
        }
    }
    
    private static AnalysisInformation documentToAnalysisInformation(Document document) {
        return new AnalysisInformation()
                .setAnalysisId(document.getObjectId("_id").toHexString())
                .setAnnotationGroupId(document.getString("annotationGroupId"))
                .setStatus(Enum.valueOf(Status.class, document.getString("status")));
    }
    
    /**
     * Updates the status of an existing analysis.
     * 
     * @param analysisId the ID of the analysis
     * @param status the new status
     * 
     * @return whether the analysis exists
     * 
     * @throws NullPointerException if {@code status} is {@code null}
     * @throws IllegalArgumentException if {@code analysisId} is not a 24-character
     * hexadecimal string
     */
    public boolean updateStatus(String analysisId, AnalysisInformation.Status status)
            throws NullPointerException, IllegalArgumentException {
        UpdateResult result = collection.updateOne(
                eq("_id", new ObjectId(analysisId)),
                set("status", status.name())
        );
        return result.getModifiedCount() > 0;
    }
    
}