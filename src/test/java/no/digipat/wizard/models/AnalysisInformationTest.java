package no.digipat.wizard.models;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import no.digipat.wizard.models.AnalysisInformation.Status;

@RunWith(JUnitParamsRunner.class)
public class AnalysisInformationTest {
    
    @Test
    @Parameters(method = "getStatuses")
    public void testStatusToString(Status status) throws Exception {
        assertEquals(status.name().toLowerCase(), status.toString());
    }
    
    private static Status[] getStatuses() {
        return Status.values();
    }
    
    @Test
    @Parameters(method = "getStatusesPlusNull")
    public void testToJson(Status status) throws Exception {
        AnalysisInformation info = new AnalysisInformation()
                .setAnalysisId("abc")
                .setStatus(status)
                .setAnnotationGroupId(null);
        
        JSONObject json = new JSONObject(info.toJson());
        
        assertEquals(nullTest(info.getAnalysisId()), json.get("analysisId"));
        assertEquals(status == null ? JSONObject.NULL : status.toString(), json.get("status"));
        assertEquals(nullTest(info.getAnnotationGroupId()), json.get("annotationGroupId"));
    }
    
    private static Object nullTest(Object obj) {
        return obj == null ? JSONObject.NULL : obj;
    }
    
    @Test(expected = IllegalArgumentException.class)
    @Parameters(method = "getInvalidJson")
    public void testInvalidJson(String invalidJson) throws Exception {
        AnalysisInformation.fromJson(invalidJson);
    }
    
    private static String[][] getInvalidJson() {
        return new String[][] {
            {"Not JSON"},
            {"{\"analysisId\": \"abc\", \"annotationGroupId\": "
                    + "\"def\", \"status\": \"total nonsense\"}"},
            {"{\"analysisId\": \"abc\", \"annotationGroupId\": \"def\", \"status\": 1}"},
            {"{\"analysisId\": \"abc\", \"annotationGroupId\": \"def\", \"status\": [\"array\"]}"},
        };
    }
    
    @Test
    @Parameters(method = "getStatusesPlusNull")
    public void testFromJson(Status status) throws Exception {
        AnalysisInformation info = new AnalysisInformation()
                .setStatus(status)
                .setAnnotationGroupId("abc")
                .setAnalysisId("def");
        
        AnalysisInformation toJsonAndBack = AnalysisInformation.fromJson(info.toJson());
        
        assertEquals(info, toJsonAndBack);
    }
    
    private static Status[] getStatusesPlusNull() {
        return Arrays.copyOf(Status.values(), Status.values().length + 1);
    }
    
    @Test
    public void testEmptyJsonAllowed() throws Exception {
        AnalysisInformation.fromJson("{}");
    }
    
}
