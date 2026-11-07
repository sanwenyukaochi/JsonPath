package com.jayway.jsonpath.web.boot;

import com.jayway.jsonpath.web.resource.ApiResource;
import java.io.IOException;
import org.eclipse.jetty.ee11.servlet.ServletContextHandler;
import org.eclipse.jetty.ee11.servlet.ServletHolder;
import org.eclipse.jetty.ee11.webapp.WebAppContext;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class Main {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                System.out.println(
                        "Invalid usage! Port argument must be an integer (if not supplied "
                                + port
                                + " is used)");
            }
        }
        System.out.println("Server started on port: " + port);

        Server server = new Server();

        server.setConnectors(new Connector[] {createConnector(server, port)});

        ServletContextHandler context =
                new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/api");
        ServletHolder servletHolder = new ServletHolder(createJerseyServlet());
        servletHolder.setInitOrder(1);
        context.addServlet(servletHolder, "/*");

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setServer(server);
        webAppContext.setContextPath("/");
        webAppContext.setBaseResourceAsString(Main.class.getResource("/webapp").toExternalForm());

        Handler.Sequence handlers = new Handler.Sequence();
        handlers.setHandlers(new Handler[] {context, webAppContext});
        server.setHandler(handlers);

        server.start();
        server.join();
    }

    private static ServerConnector createConnector(Server s, int port) {
        ServerConnector connector = new ServerConnector(s);
        connector.setHost("0.0.0.0");
        connector.setPort(port);
        return connector;
    }

    private static ServletContainer createJerseyServlet() throws IOException {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(JacksonFeature.class);

        resourceConfig.register(new ApiResource());

        return new ServletContainer(resourceConfig);
    }
}
