/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.bootstrap;

import net.alternativmud.App;
import net.alternativmud.system.lifecycle.RunnableTask;

/**
 *
 * @author jblew
 */
public class InitGamesManager implements RunnableTask {    
    @Override
    public String getDescription() {
        return "Init Games Manager.";
    }
    

    @Override
    public boolean shouldBeExecuted() {
        return true; //execute if telnet service was not registered.
    }

    @Override
    public void execute() throws Exception {
        App.getApp().getGamesManager().bootstrap();
        
        App.getApp().getLifecycle().registerShutdownTask(new RunnableTask() {
            @Override
            public String getDescription() {
                return "Stop Games Manager";
            }

            @Override
            public boolean shouldBeExecuted() {
                return true; //jesli serwer jest zarejestrowany
            }

            @Override
            public void execute() throws Exception {
                App.getApp().getGamesManager().shutdown();
            }
        });
    }
    
}