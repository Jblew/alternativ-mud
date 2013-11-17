/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.game.panels;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.alternativmud.logic.User;
import net.alternativmud.logic.event.CloseBus;
import net.alternativmud.logic.event.ReceivedTextFromUser;
import net.alternativmud.logic.event.SendTextToUser;

/**
 *
 * @author jblew
 */
public class GlobalMenuPanel {
    private final EventBus ebus;
    private final User user;
    
    private static final String MENU = "\r\n"
            + "     +------------------------------+\r\n"
            + "     | What do you want to do?      |\r\n"
            + "     +------------------------------+\r\n"
            + "     | 1) Select character and play |\r\n"
            + "     | 2) Change password           |\r\n"
            + "     | 0) Exit game                 |\r\n"
            + "     +------------------------------+\r\n"
            + "\r\n"
            + "Type number of your option: ";
    
    public GlobalMenuPanel(EventBus ebus, User user) {
        this.ebus = ebus;
        this.user = user;
        
        ebus.post(new SendTextToUser(MENU));
    }
    
    @Subscribe
    public void messageReceived(ReceivedTextFromUser evt) {
        if(evt.getText().isEmpty()) {
            ebus.post(new SendTextToUser("Say, what do you want? "));
        } else if(evt.getText().equals("1")) {
            ebus.unregister(this);
            ebus.register(new CharacterMenuPanel(ebus, user));
        } else if(evt.getText().equals("2")) {
            ebus.post(new SendTextToUser("Sorry, this feature is now unsupported. \r\n"));
        } else if(evt.getText().equals("0") || evt.getText().equals("3")) {
            ebus.post(new SendTextToUser("Bye! Your bus will be closed soon.\r\n"));
            ebus.unregister(this);
            ebus.post(new CloseBus());
        }
        else {
            ebus.post(new SendTextToUser("There is no such option. \r\n"
                    + "So, what would you like to do?"));
        }
    }
}