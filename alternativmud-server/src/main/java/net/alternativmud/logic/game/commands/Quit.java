package net.alternativmud.logic.game.commands;

import net.alternativmud.logic.event.CloseBus;
import net.alternativmud.logic.game.Gameplay;

/**
 * @author noosekpl
 */
public class Quit implements Command {
    @Override
    public String execute(Gameplay game, String [] parts) {
           game.println("Closing connection.");
           game.quit();
           return "Bye!";
    }
}
