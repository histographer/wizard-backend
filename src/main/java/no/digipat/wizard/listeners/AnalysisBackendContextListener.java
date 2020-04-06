package no.digipat.wizard.listeners;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * A context listener that sets the context parameters required to
 * connect to the analysis backend.
 * 
 * @author Jon Wallem Anundsen
 *
 */
@WebListener
public class AnalysisBackendContextListener implements ServletContextListener {
    
    /**
     * Sets the context parameters required to connect to the analysis backend.
     * After this method has been called, the context's {@code ANALYSIS_BASE_URL}
     * attribute will have been set to an instance of {@link URL} containing the
     * base URL of the analysis backend.
     * <p>
     * The base URL is determined by the environment variable {@code COMPARE_ANALYSIS_BASE_URL}.
     * </p>
     * 
     * @param servletContextEvent the context event whose context will have its
     * parameters set
     * 
     * @throws IllegalStateException if the required environment variable is missing or has
     * an invalid value
     * 
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        String urlString = System.getenv("COMPARE_ANALYSIS_BASE_URL");
        try {
            URL url = new URL(urlString);
            servletContextEvent.getServletContext().setAttribute("ANALYSIS_BASE_URL", url);
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Environment variables have not been set correctly", e);
        }
    }
    
    /**
     * Does nothing.
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        
    }
    
}
