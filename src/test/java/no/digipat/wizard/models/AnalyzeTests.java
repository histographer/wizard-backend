package no.digipat.wizard.models;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.statements.ExpectException;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class AnalyzeTests {
    private String analysisJson;
    private Analyze analyze;

    @Before
    public void setUp() {
     analysisJson= "{\"groupId\":\"abc\",\"annotations\":[1,2,3],\"analysis\":[\"he\",\"rgb\"]}";
     analyze = new Analyze().setGroupId("abc")
             .setAnnotations(new ArrayList<Long>(){{ add(1l); add(2l); add(3l);}})
             .setAnalysis(new ArrayList<String>(){{ add("he"); add("rgb");}});
    }

    @Test
    public void toJsonFromAnalyzeModel() {
        String json = Analyze.toJsonString(analyze);
        assertEquals(json, analysisJson);
    }

    @Test
    public void toAnalyzeFromJsonString() {
        Analyze analyzeConverted = Analyze.fromJsonString(analysisJson);
        assertEquals(analyze.getAnalysis(), analyzeConverted.getAnalysis());
        assertEquals(analyze.getAnnotations(), analyzeConverted.getAnnotations());
        assertEquals(analyze.getGroupId(), analyzeConverted.getGroupId());
    }

    @Test(expected=IllegalArgumentException.class)
    public void validationTestFail() {
        Analyze analyzeFail = new Analyze();
        Analyze.validate(analyzeFail);
    }

    @Test
    public void validationTestSuccess() {
        Analyze.validate(analyze);
    }

}
