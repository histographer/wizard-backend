package no.digipat.wizard.models;

import no.digipat.wizard.models.results.AnalysisResult;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class AnalysisResultTests {
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

    @Test
    public void ResultIfTypeIsNullValidationFails() {
        AnalysisResult analysisResult = new AnalysisResult()
                .setValues(new HashMap<String, Integer>(){{
                    put("hemax", 32);
                    put("coolcat", 32);
                }});
        Set<ConstraintViolation<AnalysisResult>> violations = validator.validate(analysisResult);
        assertEquals(violations.isEmpty(), false);
        assertEquals(violations.size(), 1);
    }
    @Test
    public void ResultIfValuesValidationFails() {
        AnalysisResult analysisResult = new AnalysisResult().setType("he");
        Set<ConstraintViolation<AnalysisResult>> violations = validator.validate(analysisResult);
        assertEquals(violations.isEmpty(), false);
        assertEquals(violations.size(), 1);
    }

    @Test
    public void ResultIfMapIsEmpty() {
        AnalysisResult analysisResult = new AnalysisResult().setType("he").setValues(new HashMap<String, Integer>());
        Set<ConstraintViolation<AnalysisResult>> violations = validator.validate(analysisResult);
        assertEquals(violations.isEmpty(), false);
        assertEquals(violations.size(), 1);
    }

    @Test
    public void ResultEmptyObject() {
        AnalysisResult analysisResult = new AnalysisResult();
        Set<ConstraintViolation<AnalysisResult>> violations = validator.validate(analysisResult);
        assertEquals(violations.isEmpty(), false);
        assertEquals(violations.size(), 2);
    }

    @Test
    public void ResultIsValid() {
        Set<ConstraintViolation<AnalysisResult>> violations = validator.validate(createResult());
        assertEquals(violations.isEmpty(), true);
    }

}
