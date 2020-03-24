package no.digipat.wizard.mongodb.dao;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;

import java.util.List;

import no.digipat.wizard.models.AnnotationGroup;

/**
 * A data access object (DAO) for annotation groups.
 * 
 * @author Jon Wallem Anundsen
 *
 */
public class MongoAnnotationGroupDAO {
    
    private final MongoCollection<Document> collection;
    
    /**
     * Constructs a DAO.
     * 
     * @param client the client used to connect to the database
     * @param databaseName the name of the database
     */
    public MongoAnnotationGroupDAO(MongoClient client, String databaseName) {
        this.collection = client.getDatabase(databaseName).getCollection("AnnotationGroup");
    }
    
    /**
     * Inserts a new annotation group into the database. If the annotation group's
     * ID is {@code null}, an ID will be autogenerated.
     * 
     * @param annotationGroup the new annotation group
     * 
     * @return the ID of the newly created annotation group
     * 
     * @throws NullPointerException if {@code annotationGroup} or {@code annotationGroup.getAnnotationIds()}
     * is {@code null}
     * @throws IllegalStateException if there is already an annotation group with the same ID
     * as {@code annotationGroup}
     * @throws IllegalArgumentException if the group's ID is not {@code null} and is not a 24-character
     * hexadecimal string
     */
    public String createAnnotationGroup(AnnotationGroup annotationGroup) {
        Document document = annotationGroupToDocument(annotationGroup);
        try {
            collection.insertOne(document);
        } catch (MongoWriteException e) {
            if (e.getCode() == 11000) { // Error code 11000 indicates a duplicate key
                throw new IllegalStateException("Duplicate annotation group ID", e);
            } else {
                throw e;
            }
        }
        return document.getObjectId("_id").toHexString();
    }
    
    /**
     * Gets a specific annotation group.
     * 
     * @param id the ID of the group
     * @return the group, or {@code null} if no group with the given ID exists
     */
    public AnnotationGroup getAnnotationGroup(String id) {
        Document document = collection.find(eq("_id", new ObjectId(id))).first();
        if (document == null) {
            return null;
        } else {
            return documentToAnnotationGroup(document);
        }
    }
    
    private static Document annotationGroupToDocument(AnnotationGroup annotationGroup) {
        Document document = new Document();
        String groupId = annotationGroup.getGroupId();
        if (groupId != null) {
            document.put("_id", new ObjectId(groupId));
        }
        List<Long> annotationIds = annotationGroup.getAnnotationIds();
        if (annotationIds == null) {
            throw new NullPointerException("List of annotation IDs cannot be null");
        }
        document.put("annotationIds", annotationIds);
        return document;
    }
    
    private static AnnotationGroup documentToAnnotationGroup(Document document) {
        return new AnnotationGroup()
                .setGroupId(document.getObjectId("_id").toHexString())
                .setAnnotationIds(document.getList("annotationIds", Long.class));
    }
    
}
