/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.game.panels;

import com.google.common.eventbus.EventBus;
import net.alternativmud.logic.User;
import net.alternativmud.logic.event.SendTextToUser;
import net.alternativmud.logic.game.Gameplay;
import net.alternativmud.logic.world.characters.UCharacter;

/**
 *
 * @author jblew
 */
public class GamePanel {
    private final EventBus ebus;
    private final User user;
    private final UCharacter character;

    
    public GamePanel(EventBus ebus, User user, UCharacter character) {
        this.ebus = ebus;
        this.user = user;
        this.character = character;
        
        ebus.post(new SendTextToUser("Welcome to the game "
                +character.getName()
                +".\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\nYou are now in ALTERNATIVMUD."));
        
        //Deleguje usera do gry
        ebus.unregister(this);
        ebus.register(new Gameplay(ebus, user, character));
    }    
}
