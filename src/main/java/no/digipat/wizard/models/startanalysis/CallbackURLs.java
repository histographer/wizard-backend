package no.digipat.wizard.models.startanalysis;

import java.net.URL;

import javax.validation.constraints.NotNull;

/**
 * Represents the URLs that the analysis backend can use to send
 * results back to the Wizard middleware.
 * 
 * @author Kent Are Torvik
 *
 */
public class CallbackURLs {

    @NotNull
    private URL analysisResults;
    @NotNull
    private URL updateStatus;

    public URL getAnalysisResults() {
        return analysisResults;
    }

    /**
     * Sets the URL to which the analysis backend should send analysis results.
     * 
     * @param analysisResults the URL
     * 
     * @return this
     */
    public CallbackURLs setAnalysisResult(URL analysisResults) {
        this.analysisResults = analysisResults;
        return this;
    }

    public URL getUpdateStatus() {
        return updateStatus;
    }

    /**
     * Sets the URL to which the analysis backend should send status updates.
     * 
     * @param updateStatus the URL
     * 
     * @return this
     */
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
