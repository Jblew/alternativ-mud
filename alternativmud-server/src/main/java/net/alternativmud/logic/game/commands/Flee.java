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
public class Flee implements Command {
    @Override
    public String execute(Gameplay game, String[] parts) throws ExecutionRejectedException {
        if(game.getCharacter().isInFight()) {
            UCharacter opponent = App.getApp().getWorld().getCharactersManager()
                    .getCharacter(game.getCharacter().getFight());
            opponent.setFight("");
            game.getCharacter().setFight("");
            return "Uff...";
        }
        else throw new ExecutionRejectedException("You aren't in fight.");
    }
}