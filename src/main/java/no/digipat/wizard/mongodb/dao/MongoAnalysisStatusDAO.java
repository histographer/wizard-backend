package no.digipat.wizard.mongodb.dao;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

import no.digipat.wizard.models.AnalysisStatus;
import no.digipat.wizard.models.AnalysisStatus.Status;

/**
 * A Data Access Object (DAO) for analysis requests.
 * 
 * @author Jon Wallem Anundsen
 *
 */
public class MongoAnalysisStatusDAO {
    
    private final MongoCollection<Document> collection;
    
    public MongoAnalysisStatusDAO(MongoClient client, String databaseName) {
        this.collection = client.getDatabase(databaseName).getCollection("AnalysisStatus");
    }
    
    /**
     * Inserts a new analysis status into the database. If the ID of the
     * analysis status is {@code null}, an ID will be autogenerated. 
     * 
     * @param analysisStatus the analysis status
     * 
     * @return the ID of the inserted analysis status
     * 
     * @throws NullPointerException if {@code analysisStatus} or any of its attributes
     * is {@code null}
     * @throws IllegalArgumentException if the provided analysis ID is not either
     * {@code null} or a 24-character hexadecimal string
     * @throws IllegalStateException if an analysis with the given ID already exists
     * 
     */
    public String createAnalysisStatus(AnalysisStatus analysisStatus)
            throws NullPointerException, IllegalArgumentException, IllegalStateException {
        Document document = analysisStatusToDocument(analysisStatus);
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
    
    private static Document analysisStatusToDocument(AnalysisStatus analysisStatus) {
        Document document = new Document();
        String id = analysisStatus.getAnalysisId();
        if (id != null) {
            document.append("_id", new ObjectId(analysisStatus.getAnalysisId()));
        }
        String groupId = analysisStatus.getAnnotationGroupId();
        if (groupId == null) {
            throw new NullPointerException("Annotation group ID cannot be null");
        }
        document.append("annotationGroupId", groupId)
                .append("status", analysisStatus.getStatus().name());
        return document;
    }
    
    /**
     * Gets an analysis status from the database.
     * 
     * @param analysisId the ID of the analysis
     * 
     * @return the analysis status, or {@code null} if it does not exist
     * 
     * @throws IllegalArgumentException if {@code analysisId} is not a
     * 24-character hexadecimal string
     */
    public AnalysisStatus getAnalysisStatus(String analysisId) throws IllegalArgumentException {
        Document document = collection.find(eq("_id", new ObjectId(analysisId))).first();
        if (document == null) {
            return null;
        } else {
            return documentToAnalysisStatus(document);
        }
    }
    
    private static AnalysisStatus documentToAnalysisStatus(Document document) {
        return new AnalysisStatus()
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
    public boolean updateStatus(String analysisId, AnalysisStatus.Status status)
            throws NullPointerException, IllegalArgumentException {
        UpdateResult result = collection.updateOne(
                eq("_id", new ObjectId(analysisId)),
                set("status", status.name())
        );
        return result.getModifiedCount() > 0;
    }
    
}
