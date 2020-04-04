package no.digipat.wizard.models;

import no.digipat.wizard.models.results.AnalysisResult;
import no.digipat.wizard.models.results.Results;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class ResultsTests {
    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    private Results createResults() {
        Map<String, Float> values = new HashMap<>();
        values.put("mean", 0.333f);
        values.put("std", 0.555f);
        Map<String, Map<String, Float>> tempres= new HashMap<>();
        tempres.put("H", values);
        tempres.put("E", values);
        Map<String, Map<String, Map<String, Float>>> results= new HashMap<>();
        results.put("HE", tempres);
        return new Results().setAnnotationId(1l).setResults(results);
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
        results.setResults(null);
        Set<ConstraintViolation<Results>> violations = validator.validate(results);
        assertEquals(violations.isEmpty(), false);
        results.setResults(new HashMap<String, Map<String, Map<String, Float>>>());
        Set<ConstraintViolation<Results>> violations2 = validator.validate(results);
        assertEquals(violations2.isEmpty(), false);
    }

    @Test
    public void ResultsIsValid() {
        Set<ConstraintViolation<Results>> violations = validator.validate(createResults());
        assertEquals(violations.isEmpty(), true);
    }
}
