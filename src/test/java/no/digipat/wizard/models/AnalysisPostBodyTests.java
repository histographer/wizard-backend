package no.digipat.wizard.models;

import no.digipat.wizard.models.startanalysis.AnalysisPostBody;
import no.digipat.wizard.models.startanalysis.CallbackURLs;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class AnalysisPostBodyTests {
    private String analysisJson;
    private AnalysisPostBody analysisPostBody;

    @Before
    public void setUp() {
     analysisJson= "{\"projectId\":4,\"analysisId\":\"abc\",\"annotations\":[1,2,3],\"analysis\":[\"he\",\"rgb\"], \"callbackURLs\":{\"analysisResults\":\"localhost\", \"updateStatus\":\"localhost\"}";
     analysisPostBody = new AnalysisPostBody().setAnalysisId("abc")
             .setAnnotations(new ArrayList<Long>(){{ add(1l); add(2l); add(3l);}})
             .setAnalysis(new ArrayList<String>(){{ add("he"); add("rgb");}})
             .setProjectId(4l)
             .setCallbackURLs(new CallbackURLs().setAnalysisResults("localhost").setUpdateStatus("localhost"));
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
