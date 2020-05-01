package no.digipat.wizard.models.startanalysis;

import java.net.URL;

import javax.validation.constraints.NotNull;

public class CallbackURLs {

    @NotNull
    private URL analysisResults;
    @NotNull
    private URL updateStatus;

    public URL getAnalysisResults() {
        return analysisResults;
    }

    public CallbackURLs setAnalysisResult(URL analysisResults) {
        this.analysisResults = analysisResults;
        return this;
    }

    public URL getUpdateStatus() {
        return updateStatus;
    }

    public CallbackURLs setUpdateStatus(URL updateStatus) {
        this.updateStatus = updateStatus;
        return this;
    }

    @Override
    public String toString() {
        return "CallbackURLs{"
                + "analysisResults='" + analysisResults + "'"
                + ", updateStatus='" + updateStatus + "'"
                + "}";
    }
}
