/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.game.commands;

import net.alternativmud.App;
import net.alternativmud.logic.game.Gameplay;
import net.alternativmud.logic.world.characters.UCharacter;

/**
 *
 * @author jblew
 */
public class Kill implements Command {
    @Override
    public String execute(Gameplay game, String[] parts) throws ExecutionRejectedException {
        String out = "";
        if(parts.length < 2) {
            throw new ExecutionRejectedException("Kill who?");
        }
        UCharacter c = null;
        for (Gameplay gplay : App.getApp().getGamesManager().getGameplays()) {
            if(gplay == game) continue;
            
            if(gplay.getCharacter().getLocationCoordinates().equals(game.getCharacter().getLocationCoordinates())
                    && gplay.getCharacter().getName().startsWith(parts[1])) {
                c = gplay.getCharacter();
                break;
            }
        }
        if(c == null) {
            throw new ExecutionRejectedException("There is no such person. ");
        }
        else {
            c.setFight(game.getCharacter().getName());
            game.getCharacter().setFight(c.getName());
        }
        
        return out;
    }
}