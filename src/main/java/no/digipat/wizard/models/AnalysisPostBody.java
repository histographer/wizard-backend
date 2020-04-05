package no.digipat.wizard.models;

import com.google.gson.Gson;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 *  Builds the post body that is sent to analysis
 *
 * @author Kent Are Torvik
 *
 */
public class AnalysisPostBody {
    @NotBlank
    private String groupId;

    @NotBlank
    private String projectId;

    @NotEmpty
    private Map<String, String> callbackURLs;

    @NotEmpty
    private List<Long> annotations;
    @NotEmpty
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
            throw new NullPointerException("Analyze: Json is not set");
        }
        if(json.isEmpty()) {
            throw new NullPointerException("Analyze: Jsonstring is empty");
        }

        Gson gson = new Gson();
        AnalysisPostBody analysisPostBody = null;
        try {
            analysisPostBody = gson.fromJson(json, AnalysisPostBody.class);
        } catch (Exception e) {
            throw new RuntimeException("AnnotationGroupResults: Can not create AnnotationGroupResults from json string. Input: "+json);
        }

        if(analysisPostBody.getGroupId() == null) {
            throw new NullPointerException("AnnotationGroupResults: GroupId is empty. Input: "+json);
        }
        return analysisPostBody;
    }
    public String getGroupId() {
        return groupId;
    }

    public AnalysisPostBody setGroupId(String groupId) {
        this.groupId = groupId;
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

    public String getProjectId() {
        return projectId;
    }

    public AnalysisPostBody setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public Map<String, String> getCallbackURLs() {
        return callbackURLs;
    }

    public AnalysisPostBody setCallbackURLs(Map<String, String> callbackURLs) {
        this.callbackURLs = callbackURLs;
        return this;
    }
}

