package no.digipat.wizard.models;

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
    private String annotationId;
    private List<Result> results;

    public String getAnnotationId() {
        return annotationId;
    }

    public Results setAnnotationId(String annotationId) {
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
