package no.digipat.wizard.models;

import no.digipat.wizard.models.results.AnalysisResult;
import no.digipat.wizard.models.results.Results;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ResultsTests {
    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private AnalysisResult createResult() {
        AnalysisResult res1 = new AnalysisResult().setType("he")
                .setValues(new HashMap<String, Integer>(){{
                    put("hemax", 32);
                    put("coolcat", 32);
                }});
        return res1;
    }

    private Results createResults() {
        List<AnalysisResult> analysisResultList = new ArrayList<AnalysisResult>() {{ add(createResult()); }};
        return new Results().setAnnotationId(1l).setAnalysisResults(analysisResultList);

    }

    @Test
    public void ResultsIfAnnotationIdIsNullOrEmpty() {

        Results results = createResults();
        results.setAnnotationId(null);
        Set<ConstraintViolation<Results>> violations = validator.validate(results);
        assertEquals(violations.isEmpty(), false);
        results.setAnnotationId(1l);
        Set<ConstraintViolation<Results>> violations2 = validator.validate(results);
        assertEquals(violations2.isEmpty(), true);
    }
    @Test
    public void ResultsIfResultsAreNullOrEmpty() {

        Results results = createResults();
        results.setAnalysisResults(null);
        Set<ConstraintViolation<Results>> violations = validator.validate(results);
        assertEquals(violations.isEmpty(), false);
        results.setAnalysisResults(new ArrayList<AnalysisResult>());
        Set<ConstraintViolation<Results>> violations2 = validator.validate(results);
        assertEquals(violations2.isEmpty(), false);
    }
    @Test
    public void ResultsIsValid() {
        Set<ConstraintViolation<Results>> violations = validator.validate(createResults());
        assertEquals(violations.isEmpty(), true);
    }
}
