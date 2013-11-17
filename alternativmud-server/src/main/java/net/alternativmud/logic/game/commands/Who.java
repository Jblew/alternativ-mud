/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.game.commands;

import net.alternativmud.App;
import net.alternativmud.logic.game.Gameplay;

/**
 *
 * @author jblew
 */
public class Who implements Command {
    @Override
    public String execute(Gameplay game, String [] parts) {
        String out = "{YCurrently playing:{x\r\n";
        for (Gameplay gplay : App.getApp().getGamesManager().getGameplays()) {
            out += "  "+gplay.getCharacter().getName()+"\r\n";
        }
        return out;
    }
}
