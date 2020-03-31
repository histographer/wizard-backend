package no.digipat.wizard.models;

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

    private Result createResult() {
        Result res1 = new Result().setType("he")
                .setValues(new HashMap<String, Integer>(){{
                    put("hemax", 32);
                    put("coolcat", 32);
                }});
        return res1;
    }

    private Results createResults() {
        List<Result> resultList = new ArrayList<Result>() {{ add(createResult()); }};
        return new Results().setAnnotationId("39").setResults(resultList);

    }

    @Test
    public void ResultsIfAnnotationIdIsNullOrEmpty() {

        Results results = createResults();
        results.setAnnotationId(null);
        Set<ConstraintViolation<Results>> violations = validator.validate(results);
        assertEquals(violations.isEmpty(), false);
        results.setAnnotationId("");
        Set<ConstraintViolation<Results>> violations2 = validator.validate(results);
        assertEquals(violations2.isEmpty(), false);
    }
    @Test
    public void ResultsIfResultsAreNullOrEmpty() {

        Results results = createResults();
        results.setResults(null);
        Set<ConstraintViolation<Results>> violations = validator.validate(results);
        assertEquals(violations.isEmpty(), false);
        results.setResults(new ArrayList<Result>());
        Set<ConstraintViolation<Results>> violations2 = validator.validate(results);
        assertEquals(violations2.isEmpty(), false);
    }
    @Test
    public void ResultsIsValid() {
        Set<ConstraintViolation<Results>> violations = validator.validate(createResults());
        assertEquals(violations.isEmpty(), true);
    }
}
