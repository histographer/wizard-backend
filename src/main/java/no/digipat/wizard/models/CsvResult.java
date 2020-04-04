package no.digipat.wizard.models;

import lombok.EqualsAndHashCode;
import no.digipat.wizard.models.results.AnnotationGroupResults;

/**
 * The results of an analysis, represented in CSV format.
 * 
 * @author Jon Wallem Anundsen
 * 
 * @see AnnotationGroupResults
 *
 */
@EqualsAndHashCode
public class CsvResult {
    
    private String analysisId;
    private String data;
    
    /**
     * Sets the ID of the analysis with which the results are associated.
     * 
     * @param analysisId the analysis ID
     * 
     * @return this
     */
    public CsvResult setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
        return this;
    }
    
    public String getAnalysisId() {
        return analysisId;
    }
    
    /**
     * Sets the string data to be exported, which should be formatted as CSV.
     * 
     * @param data the data
     * 
     * @return this
     */
    public CsvResult setData(String data) {
        this.data = data;
        return this;
    }
    
    public String getData() {
        return data;
    }
    
}
