package no.digipat.wizard.models;

import lombok.EqualsAndHashCode;

/**
 * A representation of an analysis that has been started, but may
 * or may not have been succesfully completed.
 * 
 * @author Jon Wallem Anundsen
 *
 */
@EqualsAndHashCode
public class AnalysisStatus {
    
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
        FAILED;
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
    public AnalysisStatus setAnalysisId(String analysisId) {
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
    public AnalysisStatus setAnnotationGroupId(String annotationGroupId) {
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
    public AnalysisStatus setStatus(Status status) {
        this.status = status;
        return this;
    }
    
}
