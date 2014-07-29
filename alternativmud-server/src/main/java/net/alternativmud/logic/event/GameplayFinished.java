/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.event;

import net.alternativmud.logic.game.Gameplay;

/**
 *
 * @author jblew
 */
public class GameplayFinished {
    
    private final Gameplay gameplay;

    public GameplayFinished(Gameplay gameplay) {
        this.gameplay = gameplay;
    }

    public Gameplay getGameplay() {
        return gameplay;
    }
}
