package no.digipat.wizard.models;

import no.digipat.wizard.models.startanalysis.AnalysisPostBody;
import no.digipat.wizard.models.startanalysis.CallbackURLs;
import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class AnalysisPostBodyTests {
    private String analysisJson;
    private AnalysisPostBody analysisPostBody;

    @Before
    public void setUp() throws MalformedURLException {
        analysisJson = "{\"analysisId\":\"abc\",\"projectId\":4,\"annotations\":[1,2,3],"
                + "\"analysis\":[\"he\",\"rgb\"], \"callbackURLs\":"
                + "{\"analysisResults\":\"http://localhost\", \"updateStatus\":\"http://localhost\"}}";
        analysisPostBody = new AnalysisPostBody().setAnalysisId("abc")
                .setAnnotations(new ArrayList<Long>() {
                    {
                        add(1L);
                        add(2L);
                        add(3L);
                    }
                })
                .setAnalysis(new ArrayList<String>() {
                    {
                        add("he");
                        add("rgb");
                    }
                })
                .setProjectId(4L)
                .setCallbackURLs(
                        new CallbackURLs()
                        .setAnalysisResult(new URL("http://localhost"))
                        .setUpdateStatus(new URL("http://localhost"))
                );
    }


    @Test
    public void toAnalyzeFromJsonString() {
        AnalysisPostBody analysisPostBodyConverted = AnalysisPostBody.fromJsonString(analysisJson);
        assertEquals(analysisPostBody.getAnalysis(), analysisPostBodyConverted.getAnalysis());
        assertEquals(analysisPostBody.getAnnotations(),
                analysisPostBodyConverted.getAnnotations());
    }

    @Test(expected = IllegalArgumentException.class)
    public void validationTestFail() {
        AnalysisPostBody analysisPostBodyFail = new AnalysisPostBody();
        AnalysisPostBody.validate(analysisPostBodyFail);
    }

    @Test
    public void validationTestSuccess() {
        AnalysisPostBody.validate(analysisPostBody);
    }

}
