/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.bootstrap;

import net.alternativmud.App;
import net.alternativmud.system.lifecycle.RunnableTask;
import net.alternativmud.security.ConnectionMonitor;
import net.alternativmud.security.PasswordHasher;

/**
 *
 * @author jblew
 */
public class InitSecurity implements RunnableTask {    
    @Override
    public String getDescription() {
        return "Initialize security";
    }

    @Override
    public boolean shouldBeExecuted() {
        return true;
    }

    @Override
    public void execute() throws Exception {
        App.getApp().getSystemEventBus().register(new ConnectionMonitor());
        
        PasswordHasher.init();
    }
    
}