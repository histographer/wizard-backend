package no.digipat.wizard.listeners;

import java.net.URLEncoder;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.mongodb.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/**
 * Handles mongodb singleton connection.
 */
@WebListener
public class MongoDBContextListener implements ServletContextListener {


    /**
     * Initializes a mongoclient and makes it available for servlets to use.
     * @param servletContextEvent
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            ServletContext context = servletContextEvent.getServletContext();
            String host = System.getenv("WIZARD_MONGODB_HOST");
            String portString = System.getenv("WIZARD_MONGODB_PORT");
            int port = Integer.parseInt(portString);
            // Username and password do not need to be percent encoded,
            // since we're not using a URL
            String username = System.getenv("WIZARD_MONGODB_USERNAME");
            String password = System.getenv("WIZARD_MONGODB_PASSWORD");
            String database = System.getenv("WIZARD_MONGODB_DATABASE");
            CodecRegistry pojoCodecRegistry = fromRegistries(
                    MongoClient.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build())
            );
            MongoCredential credentials = MongoCredential.createCredential(username,
                    database, password.toCharArray());

            MongoClient client = new MongoClient(
                    new ServerAddress(host, port),
                    credentials,
                    MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build()
            );

            context.log("Mongoclient connected successfully at " + host + ":" + port);
            context.setAttribute("MONGO_DATABASE", database);
            context.setAttribute("MONGO_CLIENT", client);
        } catch (Exception error) {
            throw new RuntimeException("Mongoclient initialization failed", error);
        }
    }


    /**
     * When the context is destroyed, terminate the mongodb connection.
     * @param servletContextEvent
     */
    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ServletContext context = servletContextEvent.getServletContext();
        MongoClient client = (MongoClient) context.getAttribute(("MONGO_CLIENT"));
        client.close();
        context.log("Mongo connection terminated");
    }
}
