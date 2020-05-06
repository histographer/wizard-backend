package no.digipat.wizard.models;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.digipat.wizard.models.results.AnnotationGroupResults;

/**
 * A representation of information about an analysis.
 * 
 * @author Jon Wallem Anundsen
 * 
 * @see AnnotationGroupResults
 *
 */
@ToString
@EqualsAndHashCode
public class AnalysisInformation {
    
    /**
     * The status of an analysis.
     * 
     * @author Jon Wallem Anundsen
     *
     */
    public enum Status {
        /**
         * Indicates that the analysis has not yet been completed.
         */
        PENDING,
        /**
         * Indicates that the analysis has successfully been completed.
         */
        SUCCESS,
        /**
         * Indicates that the analysis failed.
         */
        FAILURE;
        
        /**
         * Gets a string representation of a status.
         * 
         * @return the name of the status converted to lower case
         */
        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }
    
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Status.class, new TypeAdapter<Status>() {
                // Custom type adapter for handling status values
                @Override
                public void write(JsonWriter out, Status value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                    } else {
                        out.value(value.toString());
                    }
                }
                @Override
                public Status read(JsonReader in) throws IOException {
                    try {
                        return Status.valueOf(in.nextString().toUpperCase());
                    } catch (IllegalStateException e) {
                        in.nextNull();
                        return null;
                    }
                }
            })
            .serializeNulls().create();
    private String analysisId;
    private String annotationGroupId;
    private Status status;
    private String groupName;
    
    /**
     * Converts this analysis information to a JSON object whose key-value
     * pairs consist of this object's property names and values.
     * 
     * @return a JSON representation of this object
     */
    public String toJson() {
        return gson.toJson(this);
    }
    
    /**
     * Converts a JSON object into an analysis information object. If a
     * key is missing, the corresponding property will be {@code null}.
     * 
     * @param json the string containing the JSON object
     * 
     * @return the analysis information object
     * 
     * @throws IllegalArgumentException if {@code json} is not valid
     */
    public static AnalysisInformation fromJson(String json) throws IllegalArgumentException {
        try {
            return gson.fromJson(json, AnalysisInformation.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public String getAnalysisId() {
        return analysisId;
    }
    
    /**
     * Sets the ID of this analysis.
     * 
     * @param analysisId the ID
     * 
     * @return this
     */
    public AnalysisInformation setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
        return this;
    }
    
    public String getAnnotationGroupId() {
        return annotationGroupId;
    }
    
    /**
     * Sets the ID of the annotation group with which this analysis is
     * associated.
     * 
     * @param annotationGroupId the annotation group ID
     * 
     * @return this
     * 
     * @see AnnotationGroup
     */
    public AnalysisInformation setAnnotationGroupId(String annotationGroupId) {
        this.annotationGroupId = annotationGroupId;
        return this;
    }
    
    public Status getStatus() {
        return status;
    }
    
    /**
     * Sets the status of this analysis.
     * 
     * @param status the status
     * 
     * @return this
     */
    public AnalysisInformation setStatus(Status status) {
        this.status = status;
        return this;
    }

    public String getGroupName() {
        return groupName;
    }

    public AnalysisInformation setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }
}
