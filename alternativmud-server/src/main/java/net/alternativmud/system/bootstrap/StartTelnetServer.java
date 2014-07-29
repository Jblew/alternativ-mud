/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.bootstrap;

import net.alternativmud.App;
import net.alternativmud.lib.debug.BusLogger;
import net.alternativmud.system.lifecycle.RunnableTask;
import net.alternativmud.system.telnetserver.TelnetServer;

/**
 *
 * @author maciek
 */
public class StartTelnetServer implements RunnableTask {    
    @Override
    public String getDescription() {
        return "Start Telnet server.";
    }

    @Override
    public boolean shouldBeExecuted() {
        return !(App.getApp().getServiceManager().isRegistered(TelnetServer.class)); //execute if telnet service was not registered.
    }

    @Override
    public void execute() throws Exception {
        final TelnetServer srv = new TelnetServer();
        srv.start();
        
        App.getApp().getLifecycle().registerShutdownTask(new RunnableTask() {
            @Override
            public String getDescription() {
                return "Stop Telnet Server";
            }

            @Override
            public boolean shouldBeExecuted() {
                return App.getApp().getServiceManager().isRegistered(TelnetServer.class); //jesli serwer jest zarejestrowany
            }

            @Override
            public void execute() throws Exception {
                srv.stop();
            }
        });
    }
    
}