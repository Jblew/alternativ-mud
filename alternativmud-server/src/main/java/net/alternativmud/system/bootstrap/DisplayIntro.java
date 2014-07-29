/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.bootstrap;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import net.alternativmud.App;
import net.alternativmud.system.lifecycle.RunnableTask;

/**
 *
 * @author jblew
 */
public class DisplayIntro implements RunnableTask {    
    @Override
    public String getDescription() {
        return "Show introduction ascii-art.";
    }

    @Override
    public boolean shouldBeExecuted() {
        return true;
    }

    @Override
    public void execute() throws Exception {
        System.out.println();
        System.out.println(Resources.toString(Resources.getResource(App.class, "intro"), Charsets.UTF_8));
        System.out.println();
    }
    
}
