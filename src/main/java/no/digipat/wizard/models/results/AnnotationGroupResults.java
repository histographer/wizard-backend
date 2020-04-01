package no.digipat.wizard.models.results;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * A representation of a group of annotations. All the setters of
 * this class return the instance on which they are called.
 *
 * @author Kent Are Torvik
 *
 */
@EqualsAndHashCode
public class AnnotationGroupResults {
    @NotBlank(message = "AnnotationGroupResults: AnalysisId can not be null or empty")
    private String analysisId;

    @NotEmpty(message = "AnnotationGroupResults: results can not be null or empty")
    private List<Results> annotations;


    public String getAnalysisId() {
        return analysisId;
    }

    public AnnotationGroupResults setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
        return this;
    }

    public List<Results> getAnnotations() {
        return annotations;
    }

    public AnnotationGroupResults setAnnotations(List<Results> annotations) {
        this.annotations = annotations;
        return this;
    }



    @Override
    public String toString() {
        return "AnnotationGroupResults{" +
                "groupId='" + analysisId + '\'' +
                ", annotations=" + annotations +
                '}';
    }

}
