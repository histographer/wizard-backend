package no.digipat.wizard.listeners;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Adds environment variables to context
 */
public class EnvironmentContextListener implements ServletContextListener {

    /**
     * Adds environment variables to context
     * @param servletContextEvent
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        Boolean DEV = Boolean.parseBoolean(System.getenv("DEVELOPMENT"));
        String wizardURL = System.getenv("WIZARD_BACKEND_URL");
        String wizardPROTOCOL = System.getenv("WIZARD_BACKEND_PROTOCOL");
        String analysisURL = System.getenv("ANALYSIS_URL");
        String analysisPROTOCOL = System.getenv("ANALYSIS_PROTOCOL");
        if(analysisURL == null || analysisURL.isEmpty()) {
            throw new NullPointerException("ANALYSIS_URL is not initiated. ANALYSIS_URL: "+analysisURL);
        }
        if(wizardPROTOCOL == null || wizardPROTOCOL.isEmpty()) {
            throw new NullPointerException("WIZARD_BACKEND_PROTOCOL is not initialized. WIZARD_BACKEND_PROTOCOL: "+wizardPROTOCOL);
        }
        if(wizardURL.isEmpty()) {
            throw new NullPointerException("WIZARD_BACKEND_URL is not initialized. WIZARD_BACKEND_URL: "+ wizardURL);
        }
        URL builtWizardBackendUrl = null;
        URL builtAnalysisUrl= null;
        if(DEV) {
            String wizardPORT= System.getenv("WIZARD_BACKEND_PORT");
            if(wizardPORT.isEmpty()) {
                throw new NullPointerException("WIZARD_BACKEND_PORT is not initialized. WIZARD_BACKEND_PORT: "+ wizardPORT);
            }
            try {
                builtWizardBackendUrl = new URL(wizardPROTOCOL+"://"+wizardURL+":"+wizardPORT);
            } catch (MalformedURLException e) {
                throw new IllegalStateException("Error creating complete WIZARD_BACKEND_URL for development. Protocol: "+wizardPROTOCOL+". URL: "+wizardURL+". PORT: "+wizardPORT);
            }
        } else {
            try {
                builtWizardBackendUrl= new URL(wizardPROTOCOL+"://"+wizardURL);
            } catch (MalformedURLException e) {
                throw new IllegalStateException("Error creating complete WIZARD_BACKEND_URL for production. Protocol: "+wizardPROTOCOL+". URL: "+wizardURL);
            }
        }
        try {
           analysis = new URL(analysisPROTOCOL+"://"+analysisURL);
        }
        context.setAttribute("ANALYSIS_URL", builtAnalysisUrl);
        context.setAttribute("WIZARD_BACKEND_URL", builtWizardBackendUrl);
        context.log("Environment variables successfully added to context");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
