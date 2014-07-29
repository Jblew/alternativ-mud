/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.bootstrap;

import net.alternativmud.App;
import net.alternativmud.system.nebus.server.TCPEBusServer;
import net.alternativmud.system.lifecycle.RunnableTask;
import net.alternativmud.system.nebus.server.TCPEBusServerTest;
import net.alternativmud.system.nebus.server.WSEBusServer;
import net.alternativmud.system.tester.Tester;
import net.alternativmud.system.web.WebServer;

/**
 *
 * @author maciek
 */
public class StartWebServer implements RunnableTask {

    @Override
    public String getDescription() {
        return "Start Web Server";
    }

    @Override
    public boolean shouldBeExecuted() {
        return !(App.getApp().getServiceManager().isRegistered(WebServer.class)); //execute if telnet service was not registered.
    }

    @Override
    public void execute() throws Exception {
        final WebServer webServer = new WebServer(App.getApp().getConfig().getWebServerConfig());
        webServer.start();
        App.getApp().getServiceManager().register(WebServer.class, webServer);

        App.getApp().getLifecycle().registerShutdownTask(new RunnableTask() {
            @Override
            public String getDescription() {
                return "Stop Web Server";
            }

            @Override
            public boolean shouldBeExecuted() {
                return App.getApp().getServiceManager().isRegistered(WebServer.class); //jesli serwer jest zarejestrowany
            }

            @Override
            public void execute() throws Exception {
                App.getApp().getServiceManager().unregister(WebServer.class);
                webServer.stop();
            }
        });
    }
}