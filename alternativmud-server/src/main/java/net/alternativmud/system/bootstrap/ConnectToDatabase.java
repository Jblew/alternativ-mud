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
public class ConnectToDatabase implements RunnableTask {
    @Override
    public String getDescription() {
        return "Connect to database";
    }

    @Override
    public boolean shouldBeExecuted() {
        return !App.getApp().getEntitiesManager().isConnected();
    }

    @Override
    public void execute() throws Exception {
        App.getApp().getEntitiesManager().connect();
        
        App.getApp().getLifecycle().registerShutdownTask(new RunnableTask() {
            @Override
            public String getDescription() {
                return "Close database ConnectionSource";
            }

            @Override
            public boolean shouldBeExecuted() {
                return App.getApp().getEntitiesManager().isConnected();
            }

            @Override
            public void execute() throws Exception {
                App.getApp().getEntitiesManager().close();
            }
        });
    }
    
}
