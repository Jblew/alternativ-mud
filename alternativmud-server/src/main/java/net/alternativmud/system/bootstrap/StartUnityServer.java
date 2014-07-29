/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.system.bootstrap;

import net.alternativmud.App;
import net.alternativmud.system.lifecycle.RunnableTask;
import net.alternativmud.system.remoteadmin.RemoteAdminServer;
import net.alternativmud.system.unityserver.UnityServer;

/**
 *
 * @author teofil
 */
public class StartUnityServer implements RunnableTask {    
    @Override
    public String getDescription() {
        return "Start Unity server";
    }

    @Override
    public boolean shouldBeExecuted() {
        return !(App.getApp().getServiceManager().isRegistered(UnityServer.class));
    }

    @Override
    public void execute() throws Exception {
        final UnityServer srv = new UnityServer(App.getApp().getSystemEventBus());
        srv.start();
        
        App.getApp().getLifecycle().registerShutdownTask(new RunnableTask() {
            @Override
            public String getDescription() {
                return "Stop Unity server";
            }

            @Override
            public boolean shouldBeExecuted() {
                return App.getApp().getServiceManager().isRegistered(UnityServer.class);
            }

            @Override
            public void execute() throws Exception {
                srv.stop();
            }
        });
    }
    
}