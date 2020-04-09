package no.digipat.wizard.servlets;

import static org.junit.Assert.*;

import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class AvailableAnalysesTest {
    
    private static URL baseUrl;
    private WebConversation conversation;
    
    @BeforeClass
    public static void setUpClass() {
        baseUrl = IntegrationTests.getBaseUrl();
    }
    
    @Before
    public void setUp() {
        conversation = new WebConversation();
    }
    
    @Test
    public void testGetAvailableAnalysisTypes() throws Exception {
        WebRequest request = new GetMethodWebRequest(baseUrl, "availableAnalysisTypes");
        WebResponse response = conversation.getResponse(request);
        
        assertEquals(response.getText() + "\n", 200, response.getResponseCode());
        assertEquals("application/json", response.getContentType());
        JSONObject json = new JSONObject(response.getText());
        JSONArray array = json.getJSONArray("analysisTypes");
        assertTrue("Array of analysis types is empty", array.toList().size() > 0);
        for (Object type : array) {
            assertTrue("Analysis type " + type + " is not a string", type instanceof String);
        }
    }
    
}
