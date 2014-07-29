/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.game.panels;

import com.google.common.base.Charsets;
import com.google.common.eventbus.EventBus;
import com.google.common.io.Resources;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.App;
import net.alternativmud.logic.event.SendTextToUser;

/**
 *
 * @author jblew
 */
public class IntroPanel {
    public IntroPanel(EventBus ebus) {
        try {
            ebus.post(new SendTextToUser(Resources.toString(Resources.getResource(App.class, "telnet-welcome"), Charsets.UTF_8)));
        } catch (IOException ex) {
            ebus.post(new SendTextToUser("Welcome to AlternativMUD"));
            Logger.getLogger(IntroPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        ebus.unregister(this);
        ebus.register(new LoginPanel(ebus));
    }
}
