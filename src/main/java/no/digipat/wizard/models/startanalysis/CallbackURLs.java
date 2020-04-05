package no.digipat.wizard.models.startanalysis;

import javax.validation.constraints.NotBlank;

public class CallbackURLs {

    @NotBlank(message = "analysisResults can not be null or empty")
    private String analysisResults;
    @NotBlank(message = "updateStatus can not be null or empty")
    private String updateStatus;

    public String getAnalysisResults() {
        return analysisResults;
    }

    public CallbackURLs setAnalysisResults(String analysisResults) {
        this.analysisResults = analysisResults;
        return this;
    }

    public String getUpdateStatus() {
        return updateStatus;
    }

    public CallbackURLs setUpdateStatus(String updateStatus) {
        this.updateStatus = updateStatus;
        return this;
    }

    @Override
    public String toString() {
        return "CallbackURLs{" +
                "analysisResults='" + analysisResults + '\'' +
                ", updateStatus='" + updateStatus + '\'' +
                '}';
    }
}
