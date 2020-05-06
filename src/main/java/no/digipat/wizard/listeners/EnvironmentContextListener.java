package no.digipat.wizard.listeners;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Adds environment variables to context.
 */
@WebListener
public class EnvironmentContextListener implements ServletContextListener {

    /**
     * Adds environment variables to context.
     * 
     * @param servletContextEvent
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        Boolean dev = Boolean.parseBoolean(System.getenv("DEVELOPMENT"));
        String wizardUrl = System.getenv("WIZARD_BACKEND_URL");
        String wizardProtocol = System.getenv("WIZARD_BACKEND_PROTOCOL");
        String analysisUrl = System.getenv("ANALYSIS_URL");
        String analysisProtocol = System.getenv("ANALYSIS_PROTOCOL");

        if (analysisUrl == null || analysisUrl.isEmpty()) {
            throw new NullPointerException("ANALYSIS_URL is not initiated. ANALYSIS_URL: "
                    + analysisUrl);
        }
        if (wizardProtocol == null || wizardProtocol.isEmpty()) {
            throw new NullPointerException("WIZARD_BACKEND_PROTOCOL is not initialized."
                    + " WIZARD_BACKEND_PROTOCOL: " + wizardProtocol);
        }
        if (wizardUrl.isEmpty()) {
            throw new NullPointerException("WIZARD_BACKEND_URL is not initialized."
                    + " WIZARD_BACKEND_URL: " + wizardUrl);
        }
        URL builtWizardBackendUrl = null;
        URL builtAnalysisUrl = null;
        if (dev) {
            String wizardPORT = System.getenv("WIZARD_BACKEND_PORT");
            if (wizardPORT.isEmpty()) {
                throw new NullPointerException("WIZARD_BACKEND_PORT is not initialized."
                        + " WIZARD_BACKEND_PORT: " + wizardPORT);
            }
            try {
                builtWizardBackendUrl = new URL(wizardProtocol + "://" + wizardUrl
                        + ":" + wizardPORT);
            } catch (MalformedURLException e) {
                throw new IllegalStateException("Error creating complete WIZARD_BACKEND_URL"
                        + " for development. Protocol: " + wizardProtocol
                        + ". URL: " + wizardUrl + ". PORT: " + wizardPORT);
            }
        } else {
            try {
                builtWizardBackendUrl = new URL(wizardProtocol + "://" + wizardUrl);
            } catch (MalformedURLException e) {
                throw new IllegalStateException("Error creating complete WIZARD_BACKEND_URL"
                        + " for production. Protocol: " + wizardProtocol + ". URL: " + wizardUrl);
            }
        }
        try {
            builtAnalysisUrl = new URL(analysisProtocol + "://" + analysisUrl);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Error creating complete ANALYSIS_URL . Protocol: "
                    + analysisProtocol + ". URL: " + wizardProtocol);
        }
        context.setAttribute("ANALYSIS_URL", builtAnalysisUrl);
        context.setAttribute("WIZARD_BACKEND_URL", builtWizardBackendUrl);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
