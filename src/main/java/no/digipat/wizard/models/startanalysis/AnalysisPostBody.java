package no.digipat.wizard.models.startanalysis;

import com.google.gson.Gson;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 *  Builds the post body that is sent to analysis
 *
 * @author Kent Are Torvik
 *
 */
public class AnalysisPostBody {
    @NotBlank(message = "analysisId can not be null or empty")
    private String analysisId;


    @NotNull(message = "projectId can not be null or empty")
    private Long projectId;

    @NotNull(message = "callbackURLs can not be empty")
    private CallbackURLs callbackURLs;

    @NotEmpty(message = "annotations can not be empty")
    private List<Long> annotations;

    @NotEmpty(message = "analysis can not be empty")
    private List<String> analysis;

    /**
     * Validates that @code{analysisPostBody} is valid.
     *
     * @param analysisPostBody the analysis post body
     */
    public static void validate(AnalysisPostBody analysisPostBody) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Set<ConstraintViolation<AnalysisPostBody>> violations = factory.getValidator().validate(analysisPostBody);

        List<String> validationsList = new ArrayList<>();
        if(!violations.isEmpty()) {
            violations.forEach(violation -> {
                validationsList.add(violation.getMessage());
            });
            throw new IllegalArgumentException(String.join("Something went wrong with validating Analyze object: ",validationsList));
        }
    }

    public static String toJsonString(AnalysisPostBody analysisPostBody) {
        Gson gson = new Gson();
        return gson.toJson(analysisPostBody);
    }

    public static AnalysisPostBody fromJsonString(String json) {
        if(json == null) {
            throw new NullPointerException("AnalysisPostBody: Json is not set");
        }
        if(json.isEmpty()) {
            throw new NullPointerException("AnalysisPostBody: Jsonstring is empty");
        }

        Gson gson = new Gson();
        AnalysisPostBody analysisPostBody = null;
        try {
            analysisPostBody = gson.fromJson(json, AnalysisPostBody.class);
        } catch (Exception e) {
            throw new RuntimeException("AnalysisPostBody: Can not create AnalysisPostBody from json string. Input: "+json);
        }

        if(analysisPostBody.getAnalysisId() == null) {
            throw new NullPointerException("AnalysisPostBody: analysisId is empty. Input: "+json);
        }
        return analysisPostBody;
    }

    public String getAnalysisId() {
        return analysisId;
    }

    public AnalysisPostBody setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
        return this;
    }

    public List<Long> getAnnotations() {
        return annotations;
    }

    public AnalysisPostBody setAnnotations(List<Long> annotations) {
        this.annotations = annotations;
        return this;
    }

    public List<String> getAnalysis() {
        return analysis;
    }

    public AnalysisPostBody setAnalysis(List<String> analysis) {
        this.analysis = analysis;
        return this;
    }

    public Long getProjectId() {
        return projectId;
    }

    public AnalysisPostBody setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public CallbackURLs getCallbackURLs() {
        return callbackURLs;
    }

    public AnalysisPostBody setCallbackURLs(CallbackURLs callbackURLs) {
        this.callbackURLs = callbackURLs;
        return this;
    }

    @Override
    public String toString() {
        return "AnalysisPostBody{" +
                "analysisId='" + analysisId + '\'' +
                ", projectId='" + projectId + '\'' +
                ", callbackURLs=" + callbackURLs +
                ", annotations=" + annotations +
                ", analysis=" + analysis +
                '}';
    }
}

