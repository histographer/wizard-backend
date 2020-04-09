package no.digipat.wizard.models.results;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * A representation of a group of results connected to an annotation id.
 * All the setters of
 * this class return the instance on which they are called.
 *
 * @author Kent Are Torvik
 *
 */
@EqualsAndHashCode
public class Results {
    @NotNull(message = "Results: annotationId can not be null")
    private Long annotationId;
    @NotEmpty(message = "Results: List can not be null or empty")
    private List<AnalysisResult> results;

    public Long getAnnotationId() {
        return annotationId;
    }

    public Results setAnnotationId(Long annotationId) {
        this.annotationId = annotationId;
        return this;
    }

    public List<AnalysisResult> getResults() {
        return results;
    }

    public Results setResults(List<AnalysisResult> results) {
        this.results = results;
        return this;
    }

    @Override
    public String toString() {
        return "Results{" +
                "annotationId=" + annotationId +
                ", results=" + results +
                '}';
    }
}
