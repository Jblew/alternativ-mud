/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.system.nebus.server;

import com.google.common.eventbus.Subscribe;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author teofil
 */
public class LimitedBusLogger {
    @Subscribe
    public void recieveMessage(Object msg) {
        String msgStr = msg.toString();
        if(!msgStr.isEmpty() && !msgStr.equals("{}")) Logger.getLogger(LimitedBusLogger.class.getName()).log(Level.INFO, msgStr);
    }
}
