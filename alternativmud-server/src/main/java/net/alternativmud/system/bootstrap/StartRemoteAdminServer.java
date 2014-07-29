/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.bootstrap;

import net.alternativmud.App;
import net.alternativmud.system.lifecycle.RunnableTask;
import net.alternativmud.system.remoteadmin.RemoteAdminServer;
import net.alternativmud.system.telnetserver.TelnetServer;

/**
 *
 * @author jblew
 */
public class StartRemoteAdminServer implements RunnableTask {    
    @Override
    public String getDescription() {
        return "Start RemoteAdmin server";
    }

    @Override
    public boolean shouldBeExecuted() {
        return !(App.getApp().getServiceManager().isRegistered(RemoteAdminServer.class)); //execute if telnet service was not registered.
    }

    @Override
    public void execute() throws Exception {
        final RemoteAdminServer srv = new RemoteAdminServer();
        srv.start();
        
        App.getApp().getLifecycle().registerShutdownTask(new RunnableTask() {
            @Override
            public String getDescription() {
                return "Stop RemoteAdmin server";
            }

            @Override
            public boolean shouldBeExecuted() {
                return App.getApp().getServiceManager().isRegistered(RemoteAdminServer.class); //jesli serwer jest zarejestrowany
            }

            @Override
            public void execute() throws Exception {
                srv.stop();
            }
        });
    }
    
}