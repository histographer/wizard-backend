package no.digipat.wizard.models;

import lombok.EqualsAndHashCode;
import no.digipat.wizard.models.results.AnnotationGroupResults;

/**
 * The results of an anlysis, represented in CSV format and encoded
 * in base 64.
 * 
 * @author Jon Wallem Anundsen
 * 
 * @see AnnotationGroupResults
 *
 */
@EqualsAndHashCode
public class ExportedAnalysisResults {
    
    private String analysisId;
    private String data;
    
    /**
     * Sets the ID of the analysis with which the results are associated.
     * 
     * @param analysisId the analysis ID
     * 
     * @return this
     */
    public ExportedAnalysisResults setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
        return this;
    }
    
    public String getAnalysisId() {
        return analysisId;
    }
    
    /**
     * Sets the string data to be exported, which should be in CSV format
     * and encoded in base 64.
     * 
     * @param data the data
     * 
     * @return this
     */
    public ExportedAnalysisResults setBase64Csv(String data) {
        this.data = data;
        return this;
    }
    
    public String getBase64Csv() {
        return data;
    }
    
}
