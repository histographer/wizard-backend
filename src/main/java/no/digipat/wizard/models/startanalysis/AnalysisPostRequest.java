package no.digipat.wizard.models.startanalysis;

import com.google.gson.Gson;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 *  Model to parse startAnalysis request
 *
 * @author Kent Are Torvik
 *
 */
public class AnalysisPostRequest {

    @NotBlank(message = "groupId can not be null or empty")
    private String groupId;

    @NotEmpty(message = "analysis can not be empty")
    private List<String> analysis;


    public static void validate(AnalysisPostRequest analysisPostRequest) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Set<ConstraintViolation<AnalysisPostRequest>> violations = factory.getValidator().validate(analysisPostRequest);
        List<String> validationsList = new ArrayList<>();
        if(!violations.isEmpty()) {
            violations.forEach(violation -> {
                validationsList.add(violation.getMessage());
            });
            throw new IllegalArgumentException(String.join("Something went wrong with validating Analyze object: ",validationsList));
        }
    }
    public String getGroupId() {
        return groupId;
    }

    public AnalysisPostRequest setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public List<String> getAnalysis() {
        return analysis;
    }

    public AnalysisPostRequest setAnalysis(List<String> analysis) {
        this.analysis = analysis;
        return this;
    }

    public static AnalysisPostRequest fromJsonString(String json) {
        if(json == null) {
            throw new NullPointerException("Analyze: Json is not set");
        }
        if(json.isEmpty()) {
            throw new NullPointerException("Analyze: Jsonstring is empty");
        }

        Gson gson = new Gson();
        AnalysisPostRequest analysisPostRequest = null;
        try {
            analysisPostRequest = gson.fromJson(json, AnalysisPostRequest.class);
        } catch (Exception e) {
            throw new RuntimeException("AnnotationGroupResults: Can not create AnnotationGroupResults from json string. Input: "+json);
        }

        if(analysisPostRequest.getAnalysis() == null) {
            throw new NullPointerException("AnnotationGroupResults: analysisId is empty. Input: "+json);
        }
        return analysisPostRequest;
    }
}
