/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.bootstrap;

import java.io.File;
import net.alternativmud.system.lifecycle.RunnableTask;

/**
 *
 * @author jblew
 */
public class CreateFileTree implements RunnableTask {    
    @Override
    public String getDescription() {
        return "Create file tree";
    }

    @Override
    public boolean shouldBeExecuted() {
        return (!new File("data").exists())
                || (!new File("data/test-reports").exists())
                || (!new File("data/db").exists());
    }

    @Override
    public void execute() throws Exception {
        File dataDir = new File("data");
        if(!dataDir.exists()) dataDir.mkdir();
        
        File testReportsDir = new File("data/test-reports");
        if(!testReportsDir.exists()) testReportsDir.mkdir();
        
        File dbDir = new File("data/db");
        if(!dbDir.exists()) dbDir.mkdir();
    }
    
}
