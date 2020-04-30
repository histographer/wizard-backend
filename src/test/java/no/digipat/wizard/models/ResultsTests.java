package no.digipat.wizard.models;

import no.digipat.wizard.models.results.AnalysisComponent;
import no.digipat.wizard.models.results.AnalysisResult;
import no.digipat.wizard.models.results.AnalysisValue;
import no.digipat.wizard.models.results.Results;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class ResultsTests {
    private static Validator validator;

    @BeforeClass
    public static void setUpClass() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }


    private Results createResults() {
        AnalysisValue value1 = new AnalysisValue().setName("mean").setVal(-0.333f);
        AnalysisValue value2 = new AnalysisValue().setName("std").setVal(-0.333f);
        AnalysisComponent analysisComponent1 = new AnalysisComponent().setName("H")
                .setComponents(new ArrayList<AnalysisValue>() {
                    {
                        add(value1);
                        add(value2);
                    }
                });
        AnalysisComponent analysisComponent2 = new AnalysisComponent().setName("E")
                .setComponents(new ArrayList<AnalysisValue>() {
                    {
                        add(value1);
                        add(value2);
                    }
                });
        AnalysisResult analysisResults = new AnalysisResult().setName("HE")
                .setComponents(new ArrayList<AnalysisComponent>() {
                    {
                        add(analysisComponent1);
                        add(analysisComponent2);
                    }
                });
        Results results = new Results().setAnnotationId(3L)
                .setResults(new ArrayList<AnalysisResult>() {
                    {
                        add(analysisResults);
                    }
                });
        return results;
    }

    @Test
    public void testResultsIfAnnotationIdIsNullOrEmpty() {
        Results results = createResults();
        results.setAnnotationId(null);
        Set<ConstraintViolation<Results>> violations = validator.validate(results);
        assertEquals(violations.isEmpty(), false);
        results.setAnnotationId(1L);
        Set<ConstraintViolation<Results>> violations2 = validator.validate(results);
        assertEquals(violations2.isEmpty(), true);
    }

    @Test
    public void testResultsIfResultsAreNullOrEmpty() {
        Results results = createResults();
        results.setResults(null);
        Set<ConstraintViolation<Results>> violations = validator.validate(results);
        assertEquals(violations.isEmpty(), false);
        results.setResults(new ArrayList<>());
        Set<ConstraintViolation<Results>> violations2 = validator.validate(results);
        assertEquals(violations2.isEmpty(), false);
    }

    @Test
    public void testResultsIsValid() {
        Set<ConstraintViolation<Results>> violations = validator.validate(createResults());
        assertEquals(violations.isEmpty(), true);
    }
}
