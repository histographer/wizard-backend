package no.digipat.wizard.models.results;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * A representation of a group of results connected to an annotation id.
 * All the setters of
 * this class return the instance on which they are called.
 *
 * @author Kent Are Torvik
 * 
 * @see AnnotationGroupResults
 */
@EqualsAndHashCode
public class Results {
    @NotNull(message = "Results: annotationId can not be null")
    private Long annotationId;
    @NotEmpty(message = "Results: List can not be null or empty")
    private List<AnalysisResult> results;

    /**
     * Gets the ID of the annotation with which the results are associated.
     * 
     * @return the annotation ID
     */
    public Long getAnnotationId() {
        return annotationId;
    }

    public Results setAnnotationId(Long annotationId) {
        this.annotationId = annotationId;
        return this;
    }

    /**
     * Gets the annotation's analysis results.
     * 
     * @return the results
     */
    public List<AnalysisResult> getResults() {
        return results;
    }

    public Results setResults(List<AnalysisResult> results) {
        this.results = results;
        return this;
    }

    @Override
    public String toString() {
        return "Results{"
                + "annotationId=" + annotationId
                + ", results=" + results
                + "}";
    }
}
