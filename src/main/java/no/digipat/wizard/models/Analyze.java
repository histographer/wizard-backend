package no.digipat.wizard.models;

import com.google.gson.Gson;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Analyze {
    @NotBlank
    private String groupId;
    @NotEmpty
    private List<String> annotations;
    @NotEmpty
    private List<String> analysis;


    public static void validate(Analyze analyze) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Set<ConstraintViolation<Analyze>> violations = factory.getValidator().validate(analyze);

        List<String> validationsList = new ArrayList<>();

        if(!violations.isEmpty()) {
            violations.forEach(violation -> {
                validationsList.add(violation.getMessage());
            });
            throw new IllegalArgumentException(String.join("Something went wrong with validating Analyze object: ",validationsList));
        }
    }

    public static String toJsonString(Analyze analyze) {
        Gson gson = new Gson();
        return gson.toJson(analyze);
    }

    public static Analyze fromJsonString(String json) {
        if(json == null) {
            throw new NullPointerException("Analyze: Json is not set");
        }
        if(json.isEmpty()) {
            throw new NullPointerException("Analyze: Jsonstring is empty");
        }

        Gson gson = new Gson();
        Analyze analyze = null;
        try {
            analyze = gson.fromJson(json, Analyze.class);
        } catch (Exception e) {
            throw new RuntimeException("AnnotationGroupResults: Can not create AnnotationGroupResults from json string. Input: "+json);
        }

        if(analyze.getGroupId() == null) {
            throw new NullPointerException("AnnotationGroupResults: GroupId is empty. Input: "+json);
        }
        return analyze;
    }
    public String getGroupId() {
        return groupId;
    }

    public Analyze setGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public List<String> getAnnotations() {
        return annotations;
    }

    public Analyze setAnnotations(List<String> annotations) {
        this.annotations = annotations;
        return this;
    }

    public List<String> getAnalysis() {
        return analysis;
    }

    public Analyze setAnalysis(List<String> analysis) {
        this.analysis = analysis;
        return this;
    }

}
