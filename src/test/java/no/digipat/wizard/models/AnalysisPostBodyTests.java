package no.digipat.wizard.models;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class AnalysisPostBodyTests {
    private String analysisJson;
    private AnalysisPostBody analysisPostBody;

    @Before
    public void setUp() {
     analysisJson= "{\"groupId\":\"abc\",\"annotations\":[1,2,3],\"analysis\":[\"he\",\"rgb\"]}";
     analysisPostBody = new AnalysisPostBody().setGroupId("abc")
             .setAnnotations(new ArrayList<Long>(){{ add(1l); add(2l); add(3l);}})
             .setAnalysis(new ArrayList<String>(){{ add("he"); add("rgb");}});
    }

    @Test
    public void toJsonFromAnalyzeModel() {
        String json = AnalysisPostBody.toJsonString(analysisPostBody);
        assertEquals(json, analysisJson);
    }

    @Test
    public void toAnalyzeFromJsonString() {
        AnalysisPostBody analysisPostBodyConverted = AnalysisPostBody.fromJsonString(analysisJson);
        assertEquals(analysisPostBody.getAnalysis(), analysisPostBodyConverted.getAnalysis());
        assertEquals(analysisPostBody.getAnnotations(), analysisPostBodyConverted.getAnnotations());
        assertEquals(analysisPostBody.getGroupId(), analysisPostBodyConverted.getGroupId());
    }

    @Test(expected=IllegalArgumentException.class)
    public void validationTestFail() {
        AnalysisPostBody analysisPostBodyFail = new AnalysisPostBody();
        AnalysisPostBody.validate(analysisPostBodyFail);
    }

    @Test
    public void validationTestSuccess() {
        AnalysisPostBody.validate(analysisPostBody);
    }

}
