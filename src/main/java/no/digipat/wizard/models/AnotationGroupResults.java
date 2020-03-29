package no.digipat.wizard.models;

import java.util.List;

public class AnotationGroupResults {
    private String annotationId;
    private String groupId;
    private List<Result> results;

    public String getAnnotationId() {
        return annotationId;
    }

    public AnotationGroupResults setAnnotationId(String annotationId) {
        this.annotationId = annotationId;
        return this;
    }

    public String getGroupId() {
        return groupId;
    }

    public AnotationGroupResults setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public List<Result> getResults() {
        return results;
    }

    public AnotationGroupResults setResults(List<Result> results) {
        this.results = results;
        return this;
    }
}
