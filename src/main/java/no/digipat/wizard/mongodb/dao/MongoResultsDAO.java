package no.digipat.wizard.mongodb.dao;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import no.digipat.wizard.models.results.AnnotationGroupResults;
import no.digipat.wizard.models.results.AnalysisResult;
import no.digipat.wizard.models.results.AnnotationGroupResultsRequestBody;
import no.digipat.wizard.models.results.Results;
import com.google.gson.Gson;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.mongodb.client.model.Filters.eq;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * A data access object (DAO) for annotation group results.
 *
 * @author Kent Are Torvik
 */
public class MongoResultsDAO {
    private final MongoCollection<AnnotationGroupResults> collection;
    private final Validator validator;

    /**
     * Constructs a DAO.
     *
     * @param client       the client used to connect to the database
     * @param databaseName the name of the database
     */
    public MongoResultsDAO(MongoClient client, String databaseName) {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings
                .getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoDatabase database = client.getDatabase(databaseName)
                .withCodecRegistry(pojoCodecRegistry);
        this.collection = database.getCollection("AnnotationGroupResults",
                AnnotationGroupResults.class);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * Creates annotation group results.
     *
     * @param annotationGroupResults the annotation group results
     * 
     * @throws IllegalArgumentException if the results are invalid
     * @throws IllegalStateException if an instance of {@code AnnotationGroupResults} with the
     * same group ID already exists in the database
     */
    public void createAnnotationGroupResults(AnnotationGroupResults annotationGroupResults)
            throws IllegalArgumentException, IllegalStateException {
        validateAnnotationGroupResults(annotationGroupResults);
        try {
            collection.insertOne(annotationGroupResults);
        } catch (MongoWriteException e) {
            if (e.getCode() == 11000) { // Error code 11000 indicates a duplicate key
                throw new IllegalStateException("Duplicate group ID", e);
            } else {
                throw e;
            }
        }
    }

    /**
     * Creates results for an annotation group, or update the existing
     * results if they already exist.
     * 
     * @param annotationGroupResults the annotation group results
     */
    public void createAndUpdateResults(AnnotationGroupResults annotationGroupResults) {
        AnnotationGroupResults res = collection.find(
                eq("_id", annotationGroupResults.getGroupId())
            ).first();
        if (res == null) {
            createAnnotationGroupResults(annotationGroupResults);
        } else {
            List<Results> annotations = res.getAnnotations();
            List<Results> newAnnotations = annotationGroupResults.getAnnotations();
            for (int i = 0; i < annotations.size(); i++) {
                for (int j = 0; j < newAnnotations.size(); j++) {
                    // Check if it is the same annotation
                    if (annotations.get(i).getAnnotationId()
                            == newAnnotations.get(j).getAnnotationId()) {
                        // Need to check if the annotation exists in the scope
                        List<AnalysisResult> oldAnalysisResults = annotations
                                .get(i).getResults();
                        List<AnalysisResult> newAnalysisResults = newAnnotations
                                .get(j).getResults();
                        Set<AnalysisResult> set = new LinkedHashSet<>(oldAnalysisResults);
                        set.addAll(newAnalysisResults);

                        List<AnalysisResult> combinedList = new ArrayList<>(set);
                        annotations.get(i).setResults(combinedList);
                    }
                }
            }
            res.setAnnotations(annotations);
            collection.findOneAndReplace(eq("_id", annotationGroupResults.getGroupId()), res);
        }
    }



    /**
     * Validates annotation group results.
     *
     * @param annotationGroupResults the annotation group results
     * @throws IllegalArgumentException if it doesn't pass validation
     */
    public void validateAnnotationGroupResults(AnnotationGroupResults annotationGroupResults)
            throws IllegalArgumentException {
        List<String> validationsList = new ArrayList<String>();
        Set<ConstraintViolation<AnnotationGroupResults>> violations = validator
                .validate(annotationGroupResults);
        if (!violations.isEmpty()) {
            violations.forEach(violation -> {
                validationsList.add(violation.getMessage());
            });
            throw new IllegalArgumentException(
                    "Something went wrong with validating AnnotationGroupResults: "
                    + validationsList
            );
        }
        
        AtomicReference<Integer> annotationGroupResultsListIndex = new AtomicReference<>(0);
        annotationGroupResults.getAnnotations().forEach(results -> {
            Set<ConstraintViolation<Results>> resultsViolations = validator.validate(results);
            AtomicReference<Integer> resultsIndex = new AtomicReference<>(0);
            if (!resultsViolations.isEmpty()) {
                resultsViolations.forEach(violation -> {
                    validationsList.add("AnnotationGroupResults.Results["
                            + annotationGroupResultsListIndex + "]: " + violation.getMessage());
                });
            }
            annotationGroupResultsListIndex.getAndSet(annotationGroupResultsListIndex.get() + 1);
        });
        if (!validationsList.isEmpty()) {
            throw new IllegalArgumentException(
                    "Something went wrong with validating: " + validationsList
            );
        }
    }

    /**
     * Gets an instance of {@code AnnotationGroupResults} from the database.
     *
     * @param groupId the annotation group ID
     * @return the results, or {@code null} if the results don't exist
     */
    public AnnotationGroupResults getResults(String groupId) {
        return collection.find(eq("_id", groupId)).first();
    }

    /**
     * Converts a JSON string to annotation group results.
     * 
     * @param json the JSON string
     * 
     * @return the annotationGroupResults object
     * 
     * @throws NullPointerException if {@code json} is {@code null}
     * @throws IllegalArgumentException if {@code json} is not valid
     */
    public static AnnotationGroupResults jsonToAnnotationGroupResults(String json)
            throws IllegalArgumentException, NullPointerException {
        if (json == null) {
            throw new NullPointerException("AnnotationGroupResults: Json is not set");
        }
        if (json.isEmpty()) {
            throw new IllegalArgumentException("AnnotationGroupResults: Jsonstring is empty");
        }

        Gson gson = new Gson();
        AnnotationGroupResults annotationGroupResults = null;
        try {
            annotationGroupResults = gson.fromJson(json, AnnotationGroupResults.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("AnnotationGroupResults: Can not create"
                    + " AnnotationGroupResults from json string. Input: " + json
                    + ". Error: " + e);
        }

        if (annotationGroupResults.getGroupId() == null) {
            throw new NullPointerException("AnnotationGroupResults: GroupId is empty. Input: "
                    + json);
        }

        return annotationGroupResults;
    }

    /**
     * Converts a JSON string to an {@code AnnotationGroupResultsRequestBody}.
     * 
     * @param json the JSON string
     * @return the {@code AnnotationGroupResultsRequestBody}
     * @throws IllegalArgumentException if {@code json} is not valid
     * @throws NullPointerException if {@code json} is {@code null} or is missing
     * an analysis ID
     */
    public static AnnotationGroupResultsRequestBody jsonToAnnotationGroupResultsRequestBody(
            String json) throws IllegalArgumentException, NullPointerException {
        if (json == null) {
            throw new NullPointerException("AnnotationGroupResultsRequestBody: Json is not set");
        }
        if (json.isEmpty()) {
            throw new IllegalArgumentException("AnnotationGroupResultsRequestBody:"
                    + " Jsonstring is empty");
        }

        Gson gson = new Gson();
        AnnotationGroupResultsRequestBody annotationGroupResults = null;
        try {
            annotationGroupResults = gson.fromJson(json, AnnotationGroupResultsRequestBody.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("AnnotationGroupResultsRequestBody:"
                    + " Can not create AnnotationGroupResults from json string. Input: " + json
                    + ". Error: " + e);
        }

        if (annotationGroupResults.getAnalysisId() == null) {
            throw new NullPointerException(
                    "AnnotationGroupResultsRequestBody: analysisId is empty. Input: " + json
            );
        }

        return annotationGroupResults;
    }

    /**
     * Converts annotation group results to a JSON string.
     *
     * @param annotationGroupResults the annotation group results
     * @param annotationGroupName the annotation group name
     * @return the JSON string
     */
    public static String annotationGroupResultsToJson(
            AnnotationGroupResults annotationGroupResults, String annotationGroupName) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(annotationGroupResults);
        JsonElement element = gson.fromJson(jsonString, JsonElement.class);
        JsonObject json = element.getAsJsonObject();
        json.addProperty("groupName", annotationGroupName);
        return gson.toJson(json);
    }

}
