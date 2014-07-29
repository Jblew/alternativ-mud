/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.game.panels;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.alternativmud.App;
import net.alternativmud.lib.util.TypeUtils;
import net.alternativmud.logic.User;
import net.alternativmud.logic.event.ReceivedTextFromUser;
import net.alternativmud.logic.event.SendTextToUser;
import net.alternativmud.logic.world.characters.UCharacter;

/**
 *
 * @author jblew
 */
public class CharacterMenuPanel {
    private final EventBus ebus;
    private final User user;
    
    public CharacterMenuPanel(EventBus ebus, User user) {
        this.ebus = ebus;
        this.user = user;
        
        showMenu();
    }
    
    @Subscribe
    public void messageReceived(ReceivedTextFromUser evt) {
        if(evt.getText().isEmpty()) {
            ebus.post(new SendTextToUser("What character do you want to play? "));
        } else if(evt.getText().equals("-1")) {
            ebus.post(new SendTextToUser("Sorry, this feature is now unsupported.\r\n"));
        } else if(evt.getText().equals("-2")) {
            ebus.post(new SendTextToUser("Sorry, this feature is now unsupported. \r\n"));
        } else if(evt.getText().equals("-3")) {
            ebus.unregister(this);
            ebus.register(new GlobalMenuPanel(ebus, user));
        } else {
            String characterName = "";
            if(TypeUtils.isInteger(evt.getText())) {
                characterName = App.getApp().getWorld().getCharactersManager()
                        .getCharactersForUser(user.getLogin()).get(Integer.valueOf(evt.getText())-1).getName();
            }
            else {
                characterName = evt.getText();
            }
            UCharacter c = App.getApp().getWorld().getCharactersManager().getCharacter(characterName);
            if(c == null) {
                ebus.post(new SendTextToUser("There is no such character. \r\n"));
            }
            else {
                ebus.unregister(this);
                ebus.register(new GamePanel(ebus, user, c));
            }
        }
    }
    
    private void showMenu() {
        String out = "\r\n"
                + "{GWhich character do you want to play?{x\r\n";
        int optionNum = 0;
        for(UCharacter c : App.getApp().getWorld().getCharactersManager().getCharactersForUser(user.getLogin())) {
            out += "   "+(optionNum+1)+") "+c.getName()+"\r\n";
            optionNum++;
        }
        out += ""
                + "---\r\n"
                + "  -1) {XCreate new{x\r\n"
                + "  -2) Delete\r\n"
                + "  -3) Go back to menu"
                + "\r\n"
                + "{CSelect number or type character name: {x";
        
        ebus.post(new SendTextToUser(out));
    }
}