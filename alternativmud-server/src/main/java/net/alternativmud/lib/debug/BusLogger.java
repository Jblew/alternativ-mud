/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.debug;

import com.google.common.eventbus.Subscribe;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ta klasa nasłuchuje i zapisuje do stdout wszystkie komunikaty wybranej szyny wydarzeń (EventBus).
 * @author jblew
 */
public class BusLogger {
    /**
     * NoosekPL: Funkcje opatrone adnotacją @Subscribe są automatycznie wykonywane dla zarejestrowanego obiektu.
     */
    @Subscribe
    public void recieveMessage(Object msg) {
        Logger.getLogger(BusLogger.class.getName()).log(Level.INFO, msg.toString());
    }
}
