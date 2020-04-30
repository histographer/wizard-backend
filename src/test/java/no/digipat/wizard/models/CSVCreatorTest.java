package no.digipat.wizard.models;

import no.digipat.wizard.models.results.AnalysisComponent;
import no.digipat.wizard.models.results.AnalysisResult;
import no.digipat.wizard.models.results.AnalysisValue;
import no.digipat.wizard.models.results.Results;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.ArrayList;

public class CSVcreatorTest {
    
    @ClassRule
    public static final TemporaryFolder folder = new TemporaryFolder();
    
    private Results createResults(String analysisType) {
        AnalysisValue value1 = new AnalysisValue().setName("mean").setVal(-0.333f);
        AnalysisValue value2 = new AnalysisValue().setName("std").setVal(-0.333f);
        AnalysisComponent analysisComponent1 = new AnalysisComponent().setName("H").setComponents(new ArrayList(){{add(value1); add(value2);}});
        AnalysisComponent analysisComponent2 = new AnalysisComponent().setName("E").setComponents(new ArrayList(){{add(value1); add(value2);}});
        AnalysisResult analysisResults = new AnalysisResult().setName(analysisType).setComponents(new ArrayList(){{add(analysisComponent1); add(analysisComponent2);}});
        Results results = new Results().setAnnotationId(3l).setResults(new ArrayList(){{add(analysisResults);}});
        return results;
    }

    @Test
    public void createCSVfromResultsTest() throws Exception {
        String str;
        str = CSVcreator.toCSV(new ArrayList<Results>(){{
            add(createResults("he"));
            add(createResults("AB"));
        }}, folder.getRoot().getAbsolutePath(), "he");
        // If an exception is not thrown, then everything went as planned
    }
}
