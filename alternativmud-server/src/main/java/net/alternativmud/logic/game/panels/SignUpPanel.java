/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.game.panels;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.alternativmud.logic.event.ReceivedTextFromUser;
import net.alternativmud.logic.event.SendTextToUser;

/**
 * Panel tworzenia konta.
 * @author jblew
 */

class SignUpPanel {
    private final EventBus ebus;
    private SignUpPanel.State state = SignUpPanel.State.NAME_PROMPT;
    private String login = "";
    public SignUpPanel(EventBus ebus) {
        this.ebus = ebus;
        
        ebus.post(new SendTextToUser("Welcome to the world of AlternativMUD. Sorry, but this feature is now unsupported."));
    }
    
    @Subscribe
    public void messageReceived(ReceivedTextFromUser evt) {
        if(state == SignUpPanel.State.NAME_PROMPT) {
            
        }
    }
    
    private static enum State {
        NAME_PROMPT, PASSWORD_PROMPT, EMAIL_PROMPT
    }
}
