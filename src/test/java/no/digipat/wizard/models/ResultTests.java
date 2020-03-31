package no.digipat.wizard.models;

import no.digipat.wizard.mongodb.dao.MongoResultsDAO;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class ResultTests {
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

    @Test
    public void ResultIfTypeIsNullValidationFails() {
        Result result = new Result()
                .setValues(new HashMap<String, Integer>(){{
                    put("hemax", 32);
                    put("coolcat", 32);
                }});
        Set<ConstraintViolation<Result>> violations = validator.validate(result);
        assertEquals(violations.isEmpty(), false);
        assertEquals(violations.size(), 1);
    }
    @Test
    public void ResultIfValuesValidationFails() {
        Result result = new Result().setType("he");
        Set<ConstraintViolation<Result>> violations = validator.validate(result);
        assertEquals(violations.isEmpty(), false);
        assertEquals(violations.size(), 1);
    }

    @Test
    public void ResultIfMapIsEmpty() {
        Result result = new Result().setType("he").setValues(new HashMap<String, Integer>());
        Set<ConstraintViolation<Result>> violations = validator.validate(result);
        assertEquals(violations.isEmpty(), false);
        assertEquals(violations.size(), 1);
    }

    @Test
    public void ResultEmptyObject() {
        Result result = new Result();
        Set<ConstraintViolation<Result>> violations = validator.validate(result);
        assertEquals(violations.isEmpty(), false);
        assertEquals(violations.size(), 2);
    }

    @Test
    public void ResultIsValid() {
        Set<ConstraintViolation<Result>> violations = validator.validate(createResult());
        assertEquals(violations.isEmpty(), true);
    }





}
