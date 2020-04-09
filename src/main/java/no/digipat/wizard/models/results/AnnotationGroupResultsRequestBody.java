package no.digipat.wizard.models.results;

import lombok.EqualsAndHashCode;
import org.bson.codecs.pojo.annotations.BsonId;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@EqualsAndHashCode
public class AnnotationGroupResultsRequestBody {
    @NotBlank(message = "AnnotationGroupResults: AnalysisId can not be null or empty")
    @BsonId
    private String analysisId;

    @NotEmpty(message = "AnnotationGroupResults: results can not be null or empty")
    private List<Results> annotations;

    public String getAnalysisId() {
        return analysisId;
    }

    public AnnotationGroupResultsRequestBody setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
        return this;
    }

    public List<Results> getAnnotations() {
        return annotations;
    }

    public AnnotationGroupResultsRequestBody setAnnotations(List<Results> annotations) {
        this.annotations = annotations;
        return this;
    }

    @Override
    public String toString() {
        return "AnnotationGroupResultsRequestBody{" +
                "analysisId='" + analysisId + '\'' +
                ", annotations=" + annotations +
                '}';
    }
}
