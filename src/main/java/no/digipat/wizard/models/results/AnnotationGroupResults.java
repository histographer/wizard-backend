package no.digipat.wizard.models.results;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.bson.codecs.pojo.annotations.BsonId;

import lombok.EqualsAndHashCode;
import no.digipat.wizard.models.AnalysisInformation;

import java.util.List;

/**
 * A representation of analysis results for a group of annotations.
 * All the setters of this class return the instance on which they are called.
 *
 * @author Kent Are Torvik
 * 
 * @see AnalysisInformation
 *
 */
@EqualsAndHashCode
public class AnnotationGroupResults {
    @NotBlank(message = "AnnotationGroupResults: AnalysisId can not be null or empty")
    @BsonId
    private String groupId;

    @NotEmpty(message = "AnnotationGroupResults: results can not be null or empty")
    private List<Results> annotations;

    /**
     * Gets the ID of the annotation group with which the results are associated.
     * 
     * @return the group ID
     */
    public String getGroupId() {
        return groupId;
    }

    public AnnotationGroupResults setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    /**
     * Gets the list of results associated with the individual
     * annotations in the group.
     * 
     * @return a list of {@code Results} instances, each one
     * being associated with a single annotation
     */
    public List<Results> getAnnotations() {
        return annotations;
    }

    public AnnotationGroupResults setAnnotations(List<Results> annotations) {
        this.annotations = annotations;
        return this;
    }


    @Override
    public String toString() {
        return "AnnotationGroupResults{"
                + "groupId='" + groupId + "'"
                + ", annotations=" + annotations
                + "}";
    }
}
