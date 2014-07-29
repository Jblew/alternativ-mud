package net.alternativmud.logic.game.commands;

import net.alternativmud.logic.game.Gameplay;

/**
 *
 * @author noosekpl
 */
public interface Command {
    public String execute(Gameplay game, String [] parts) throws ExecutionRejectedException;
}
