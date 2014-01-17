/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.web;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.framework.ExternalService;
import net.alternativmud.system.web.servlets.Servlets;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 *
 * @author jblew
 */
public class WebServer implements ExternalService {
    private final WebServerConfig config;
    private final Server server;
    private final AtomicBoolean isBound = new AtomicBoolean(false);

    public WebServer(WebServerConfig config) {
        this.config = config;
        server = new Server(new InetSocketAddress(config.host, config.port));
        
        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setResourceBase(config.webResourcesPath);

        ContextHandler resourceContext = new ContextHandler();
        resourceContext.setContextPath(config.webResourcesContextPath);
        resourceContext.setResourceBase(".");
        resourceContext.setClassLoader(Thread.currentThread().getContextClassLoader());
        resourceContext.setHandler(resource_handler);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.setContextPath("/");
        server.setHandler(context);
        for (String path : Servlets.servlets.keySet()) {
            context.addServlet(Servlets.servlets.get(path), path);
        }

        HandlerList handlers = new HandlerList();

        handlers.setHandlers(new Handler[]{resourceContext, context});
        server.setHandler(handlers);
    }

    public void bind() {
        try {
            server.start();
            server.join();
            Logger.getLogger("WebServer").log(Level.INFO, "WebServer bound");
        } catch (Exception ex) {
            Logger.getLogger(WebServer.class.getName()).log(Level.SEVERE, "Could not bind web server", ex);
        }
    }

    public void start() {
        try {
            server.start();
        } catch (Exception ex) {
            Logger.getLogger("WebServer").log(Level.SEVERE, null, ex);
        }
    }

    public void stop() {
        try {
            server.stop();
        } catch (Exception ex) {
            Logger.getLogger("WebServer").log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int getPort() {
        return config.getPort();
    }

    @Override
    public boolean isBind() {
        return isBound.get();
    }

    @Override
    public ProtocolType getProtocolType() {
        return ProtocolType.TCP;
    }

    @Override
    public String getDescription() {
        return "Web Server";
    }
    
    public static class WebServerConfig {
        private String host = "0.0.0.0";
        private int port = 7800;
        private String webResourcesPath = "./web-resources/";
        private String webResourcesContextPath = "/resources";

        public WebServerConfig() {
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getWebResourcesPath() {
            return webResourcesPath;
        }

        public void setWebResourcesPath(String webResourcesPath) {
            this.webResourcesPath = webResourcesPath;
        }

        public String getWebResourcesContextPath() {
            return webResourcesContextPath;
        }

        public void setWebResourcesContextPath(String webResourcesContextPath) {
            this.webResourcesContextPath = webResourcesContextPath;
        }
    }
}