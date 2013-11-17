package net.alternativmud.logic.game.commands;

import net.alternativmud.logic.game.Gameplay;

/**
 * @author noosekpl
 */
public class Help implements Command {

    @Override
    public String execute(Gameplay game, String [] parts) {
        return ""
                + "Commands:\r\n"
                + "  help - show this help\r\n"
                + "  say - allows for communication\r\n"
                + "  quit - out of the game\r\n";
    }
}
