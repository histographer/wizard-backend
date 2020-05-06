package no.digipat.wizard.models;

import no.digipat.wizard.models.results.AnalysisComponent;
import no.digipat.wizard.models.results.AnalysisResult;
import no.digipat.wizard.models.results.AnalysisValue;
import no.digipat.wizard.models.results.Results;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class CSVCreatorTest {
    
    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();
    
    private Results createResults(String analysisType) {
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
        AnalysisResult analysisResults = new AnalysisResult().setName(analysisType)
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
    @Parameters(method = "getCsvParameters")
    public void createCSVfromResultsTest(String analyzeType, String expectedCsv) throws Exception {
        String actualCsv = CSVCreator.toCSV(
                new ArrayList<Results>() {
                    {
                        add(createResults("he"));
                        add(createResults("AB"));
                    }
                },
                folder.getRoot().getAbsolutePath(),
                analyzeType
        );
        assertEquals(expectedCsv, actualCsv);
    }
    
    private static String[][] getCsvParameters() {
        return new String[][] {
            {"he", "YW5ub3RhdGlvbklkLHJlc3VsdHNfbmFtZSxyZXN1bHRzX2NvbXBvbmVudHNfbmFtZ"
                    + "SxyZXN1bHRzX2NvbXBvbmVudHNfY29tcG9uZW50c19uYW1lLHJlc3Vsd"
                    + "HNfY29tcG9uZW50c19jb21wb25lbnRzX3ZhbAozLjAsImhlIiwiSCIsI"
                    + "m1lYW4iLC0wLjMzMwozLjAsImhlIiwiSCIsInN0ZCIsLTAuMzMzCjMuM"
                    + "CwiaGUiLCJFIiwibWVhbiIsLTAuMzMzCjMuMCwiaGUiLCJFIiwic3RkIiwtMC4zMzMK"
            },
            {null, "YW5ub3RhdGlvbklkLHJlc3VsdHNfbmFtZSxyZXN1bHRzX2NvbXBvbmVudHN"
                    + "fbmFtZSxyZXN1bHRzX2NvbXBvbmVudHNfY29tcG9uZW50c19uYW1lLHJ"
                    + "lc3VsdHNfY29tcG9uZW50c19jb21wb25lbnRzX3ZhbAozLjAsImhlIiw"
                    + "iSCIsIm1lYW4iLC0wLjMzMwozLjAsImhlIiwiSCIsInN0ZCIsLTAuMzM"
                    + "zCjMuMCwiaGUiLCJFIiwibWVhbiIsLTAuMzMzCjMuMCwiaGUiLCJFIiw"
                    + "ic3RkIiwtMC4zMzMKMy4wLCJBQiIsIkgiLCJtZWFuIiwtMC4zMzMKMy4"
                    + "wLCJBQiIsIkgiLCJzdGQiLC0wLjMzMwozLjAsIkFCIiwiRSIsIm1lYW4"
                    + "iLC0wLjMzMwozLjAsIkFCIiwiRSIsInN0ZCIsLTAuMzMzCg=="}
        };
    }
    
}
