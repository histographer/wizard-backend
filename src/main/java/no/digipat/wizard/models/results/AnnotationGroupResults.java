package no.digipat.wizard.models.results;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import org.bson.codecs.pojo.annotations.BsonId;

import lombok.EqualsAndHashCode;
import no.digipat.wizard.models.AnalysisInformation;

import java.util.List;

/**
 * A representation of a group of annotations. All the setters of
 * this class return the instance on which they are called.
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
