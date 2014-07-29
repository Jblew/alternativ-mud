/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.bootstrap;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import net.alternativmud.App;
import net.alternativmud.lib.debug.BusLogger;
import net.alternativmud.system.lifecycle.RunnableTask;

/**
 *
 * @author jblew
 */
public class InitBusDebug implements RunnableTask {    
    @Override
    public String getDescription() {
        return "Initializes logging of bus messages.";
    }

    @Override
    public boolean shouldBeExecuted() {
        return true;
    }

    @Override
    public void execute() throws Exception {
        App.getApp().getSystemEventBus().register(new BusLogger());
    }
    
}