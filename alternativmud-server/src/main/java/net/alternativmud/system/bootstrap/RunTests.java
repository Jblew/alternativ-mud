/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.bootstrap;

import net.alternativmud.App;
import net.alternativmud.system.lifecycle.RunnableTask;
import net.alternativmud.system.tester.Tester;

/**
 *
 * @author jblew
 */
public class RunTests implements RunnableTask {    
    @Override
    public String getDescription() {
        return "Run tests";
    }

    @Override
    public boolean shouldBeExecuted() {
        return (App.getApp().getServiceManager().isRegistered(Tester.class));
    }

    @Override
    public void execute() throws Exception {
        ((Tester)App.getApp().getServiceManager().getService(Tester.class)).test();
    }
    
}