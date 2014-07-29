/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.system.bootstrap;

import net.alternativmud.App;
import net.alternativmud.system.lifecycle.RunnableTask;

/**
 *
 * @author teofil
 */
public class InitVariablesManager implements RunnableTask {
    @Override
    public String getDescription() {
        return "Init Variables Manager";
    }

    @Override
    public boolean shouldBeExecuted() {
        return !App.getApp().getVariablesManager().isInitialized();
    }

    @Override
    public void execute() throws Exception {
        App.getApp().getVariablesManager().init();
        
        App.getApp().getLifecycle().registerShutdownTask(new RunnableTask() {
            @Override
            public String getDescription() {
                return "Close Variables Manager";
            }

            @Override
            public boolean shouldBeExecuted() {
                return App.getApp().getVariablesManager().isInitialized();
            }

            @Override
            public void execute() throws Exception {
                App.getApp().getVariablesManager().close();
            }
        });
    }
    
}
