/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.bootstrap;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.App;
import net.alternativmud.system.lifecycle.RunnableTask;
import net.alternativmud.security.ConnectionMonitor;

/**
 *
 * @author maciek
 */

public class InitPersistence implements RunnableTask {    
    @Override
    public String getDescription() {
        return "Initialize persistence manager";
    }

    @Override
    public boolean shouldBeExecuted() {
        return true;//!App.getApp().getDatabaseManager().isLoaded();
    }

    @Override
    public void execute() throws Exception {
        final AtomicBoolean shutdownComplete = new AtomicBoolean(false);
        
        App.getApp().getLifecycle().registerShutdownTask(new RunnableTask() {
            @Override
            public String getDescription() {
                return "Shutdown persistence manager";
            }

            @Override
            public boolean shouldBeExecuted() {
                return shutdownComplete.get() == false;
            }

            @Override
            public void execute() throws Exception {
                saveAndShutdown();
                shutdownComplete.set(true);
            }
        });
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                saveAndShutdown();
            }
        }));
    }
    
    private void saveAndShutdown() {
        try {
            long stimeMs = System.currentTimeMillis();
            App.getApp().getUsersManager().save(App.getApp().getPersistenceManager());
            App.getApp().getWorld().save(App.getApp().getPersistenceManager());
            App.getApp().getPersistenceManager().shutdown();
            
            //Write serialization time (for debugging purposes)
            File f = new File("data/db/serialization-time.ms");
            if(!f.exists()) f.createNewFile();
            Files.write(""+(System.currentTimeMillis()-stimeMs), f, Charsets.UTF_8);
        } catch (IOException ex) {
            Logger.getLogger(InitPersistence.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}