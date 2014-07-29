/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.bootstrap;

import net.alternativmud.App;
import net.alternativmud.system.lifecycle.RunnableTask;
import net.alternativmud.security.ConnectionMonitor;
import net.alternativmud.system.telnetserver.TelnetServer;
import net.alternativmud.system.tester.Tester;

/**
 *
 * @author jblew
 */
public class InitTester implements RunnableTask {    
    @Override
    public String getDescription() {
        return "Initialize tester";
    }

    @Override
    public boolean shouldBeExecuted() {
        return !(App.getApp().getServiceManager().isRegistered(Tester.class));
    }

    @Override
    public void execute() throws Exception {
        final Tester t = new Tester();
        t.start();
        
        App.getApp().getLifecycle().registerShutdownTask(new RunnableTask() {
            @Override
            public String getDescription() {
                return "Stop tester";
            }

            @Override
            public boolean shouldBeExecuted() {
                return App.getApp().getServiceManager().isRegistered(Tester.class); //jesli serwer jest zarejestrowany
            }

            @Override
            public void execute() throws Exception {
                t.stop();
            }
        });
    }
}