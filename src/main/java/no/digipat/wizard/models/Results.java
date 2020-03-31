package no.digipat.wizard.models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * A representation of a group of results connected to an annotation id.
 * All the setters of
 * this class return the instance on which they are called.
 *
 * @author Kent Are Torvik
 *
 */
public class Results {
    @NotNull(message = "Results: annotationId can not be null")
    private Long annotationId;
    @NotEmpty(message = "Results: List can not be null or empty")
    private List<Result> results;

    public Long getAnnotationId() {
        return annotationId;
    }

    public Results setAnnotationId(Long annotationId) {
        this.annotationId = annotationId;
        return this;
    }

    public List<Result> getResults() {
        return results;
    }

    public Results setResults(List<Result> results) {
        this.results = results;
        return this;
    }
}
