package no.digipat.wizard.mongodb.dao;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import no.digipat.wizard.models.AnnotationGroupResults;
import no.digipat.wizard.models.Result;
import no.digipat.wizard.models.Results;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import com.google.gson.Gson;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import static com.mongodb.client.model.Filters.eq;

/**
 * A data access object (DAO) for annotation group results.
 *
 * @author Kent Are Torvik
 *
 */
public class MongoResultsDAO {
    private final MongoCollection<AnnotationGroupResults> collection;
    private final Validator validator;

    /**
     * Constructs a DAO.
     *
     * @param client the client used to connect to the database
     * @param databaseName the name of the database
     */
    public MongoResultsDAO(MongoClient client, String databaseName) {
        this.collection = client.getDatabase(databaseName).getCollection("AnnotationGroupResults", AnnotationGroupResults.class);
        ValidatorFactory validatorFactory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public void createAnnotationGroupResults(AnnotationGroupResults annotationGroupResults) {
        try{
            validateAnnotationGroupResults(annotationGroupResults);
            collection.insertOne(annotationGroupResults);
        } catch (IllegalArgumentException e) {
           throw new IllegalArgumentException("Something went wrong: "+e);
        }
    }

    public void validateAnnotationGroupResults(AnnotationGroupResults annotationGroupResults) {
        List<String> validationsList = new ArrayList<String>();
       Set<ConstraintViolation<AnnotationGroupResults>> violations = validator.validate(annotationGroupResults);
       if(!violations.isEmpty()) {
           violations.forEach(violation -> {
               validationsList.add(violation.getMessage());
           });
           throw new IllegalArgumentException(String.join("Something went wrong with validating AnnotationGroupResults: ",validationsList));
       }

        AtomicReference<Integer> annotationGroupResultsListIndex = new AtomicReference<>(0);
        annotationGroupResults.getResults().forEach(results -> {
           Set<ConstraintViolation<Results>> resultsViolations = validator.validate(results);
           AtomicReference<Integer> resultsIndex = new AtomicReference<>(0);
           if(!resultsViolations.isEmpty()) {
               resultsViolations.forEach(violation -> {
                   validationsList.add("AnnotationGroupResults.Results["+annotationGroupResultsListIndex+"]: "+ violation.getMessage());
               });
           }
           results.getResults().forEach(result -> {
               Set<ConstraintViolation<Result>> resultViolations = validator.validate(result);
               if(!resultViolations.isEmpty()) {
                   resultViolations.forEach(violation -> {
                       validationsList.add("AnnotationGroupResults["+annotationGroupResultsListIndex+"]["+ resultsIndex.get()+"]: "+violation.getMessage());
                   });
               }
               resultsIndex.getAndSet(resultsIndex.get() + 1);
           });
           annotationGroupResultsListIndex.getAndSet(annotationGroupResultsListIndex.get() + 1);
       });
       if(!validationsList.isEmpty()) {
           throw new IllegalArgumentException(String.join("Soemthing went wrong with validating: ",validationsList));
       }
    }

    public List<AnnotationGroupResults> getResults(String groupId) {
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
