/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.bootstrap;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.util.logging.Handler;
import java.util.logging.Logger;
import net.alternativmud.App;
import net.alternativmud.lib.logging.SingleLineFormatter;
import net.alternativmud.system.lifecycle.RunnableTask;

/**
 *
 * @author jblew
 */
public class PrepareLogger implements RunnableTask {    
    @Override
    public String getDescription() {
        return "Prepare logger";
    }

    @Override
    public boolean shouldBeExecuted() {
        return true;
    }

    @Override
    public void execute() throws Exception {
         for(Handler h : Logger.getLogger("").getHandlers()) {
            h.setFormatter(new SingleLineFormatter(false));
        }
    }
    
}
