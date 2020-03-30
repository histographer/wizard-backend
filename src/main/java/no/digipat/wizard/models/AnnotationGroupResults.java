package no.digipat.wizard.models;

import java.util.List;

/**
 * A representation of a group of annotations. All the setters of
 * this class return the instance on which they are called.
 *
 * @author Kent Are Torvik
 *
 */
public class AnnotationGroupResults {
    private String groupId;
    private List<Results> results;


    public String getGroupId() {
        return groupId;
    }

    public AnnotationGroupResults setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public List<Results> getResults() {
        return results;
    }

    public AnnotationGroupResults setResults(List<Results> results) {
        this.results = results;
        return this;
    }
}
