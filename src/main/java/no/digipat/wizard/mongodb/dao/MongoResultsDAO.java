package no.digipat.wizard.mongodb.dao;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.util.JSON;
import no.digipat.wizard.models.AnnotationGroup;
import no.digipat.wizard.models.AnnotationGroupResults;
import no.digipat.wizard.models.Result;
import no.digipat.wizard.models.Results;
import org.bson.Document;
import org.json.JSONObject;
import com.google.gson.Gson;

import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

/**
 * A data access object (DAO) for annotation group results.
 *
 * @author Kent Are Torvik
 *
 */
public class MongoResultsDAO {
    private final MongoCollection<AnnotationGroupResults> collection;

    /**
     * Constructs a DAO.
     *
     * @param client the client used to connect to the database
     * @param databaseName the name of the database
     */
    public MongoResultsDAO(MongoClient client, String databaseName) {
        this.collection = client.getDatabase(databaseName).getCollection("AnnotationGroupResults", AnnotationGroupResults.class);
    }

    public void createAnnotationGroupResults(AnnotationGroupResults annotationGroupResults) {
       collection.insertOne(annotationGroupResults);
    }

    public List<AnnotationGroupResults> getAnnotationGroupResults(String groupId) {
        List<AnnotationGroupResults> resultArray = new ArrayList<>();
        FindIterable annotationGroupResults = collection.find(eq("groupId", groupId));

        for(Object agr : annotationGroupResults) {
           resultArray.add((AnnotationGroupResults) agr);
        }

        return resultArray;
    }

    public AnnotationGroupResults jsonToAnnotationGroupResults(String json)  {
        if(json == null) {
            throw new NullPointerException("AnnotationGroupResults: Json is not set");
        }
        if(json.isEmpty()) {
            throw new NullPointerException("AnnotationGroupResults: Jsonstring is empty");
        }

        Gson gson = new Gson();
        AnnotationGroupResults annotationGroupResults = null;
        try {
            annotationGroupResults = gson.fromJson(json, AnnotationGroupResults.class);
        } catch (Exception e) {
            throw new RuntimeException("AnnotationGroupResults: Can not create AnnotationGroupResults from json string. Input: "+json);
        }

        if(annotationGroupResults.getGroupId() == null) {
            throw new NullPointerException("AnnotationGroupResults: GroupId is empty. Input: "+json);
        }

        return annotationGroupResults;
    }

    public String annotationGroupResultsToJson(AnnotationGroupResults annotationGroupResults) {
        Gson gson = new Gson();
        return gson.toJson(annotationGroupResults);
    }

}
