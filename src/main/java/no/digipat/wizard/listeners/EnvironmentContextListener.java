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
        String wizardURL = System.getenv("WIZARD_BACKEND_URL");
        if(wizardURL.isEmpty()) {
            throw new NullPointerException("WIZARD_BACKEND_URL is not initialized. WIZARD_BACKEND_URL: "+ wizardURL);
        }
        String wizardPORT= System.getenv("WIZARD_BACKEND_PORT");
        if(wizardPORT.isEmpty()) {
            throw new NullPointerException("WIZARD_BACKEND_PORT is not initialized. WIZARD_BACKEND_PORT: "+ wizardPORT);
        }

        context.setAttribute("WIZARD_BACKEND_PORT", wizardPORT);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
