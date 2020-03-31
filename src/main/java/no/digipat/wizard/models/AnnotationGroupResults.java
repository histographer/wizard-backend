package no.digipat.wizard.models;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * A representation of a group of annotations. All the setters of
 * this class return the instance on which they are called.
 *
 * @author Kent Are Torvik
 *
 */
public class AnnotationGroupResults {
    @NotBlank(message = "GroupId can not be null or empty")
    private String groupId;

    @NotEmpty(message = "results can not be null or empty")
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
