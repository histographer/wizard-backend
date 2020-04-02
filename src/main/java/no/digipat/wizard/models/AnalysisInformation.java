package no.digipat.wizard.models;

import lombok.EqualsAndHashCode;
import no.digipat.wizard.models.results.AnnotationGroupResults;

/**
 * A representation of information about an analysis.
 * 
 * @author Jon Wallem Anundsen
 * 
 * @see AnnotationGroupResults
 *
 */
@EqualsAndHashCode
public class AnalysisInformation {
    
    /**
     * The status of an analysis.
     * 
     * @author Jon Wallem Anundsen
     *
     */
    public static enum Status {
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
    }
    
    private String analysisId;
    private String annotationGroupId;
    private Status status;
    
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
    
}
