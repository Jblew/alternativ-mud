package net.alternativmud.logic.game.commands;

import net.alternativmud.App;
import net.alternativmud.logic.game.Gameplay;

/**
 * @author noosekpl&jblew
 */
public class Say implements Command {

    @Override
    public String execute(Gameplay game, String[] parts) {
        if (parts.length < 2 || parts[1].isEmpty()) {
            return "What do you want to say?";
        }

        String text = "";
        for (int i = 1; i < parts.length; i++) {
            if (text.isEmpty()) {
                text = parts[i];
            } else {
                text += " " + parts[i];
            }
        }
        
        for (Gameplay gplay : App.getApp().getGamesManager().getGameplays()) {
            if (gplay != game) {
                if(gplay.getCharacter().getLocationCoordinates() == game.getCharacter().getLocationCoordinates())
                    gplay.println(game.getCharacter().getName() + " says '{G" + text + "{x'");
            }
        }
        
        return "You say '{g" + text + "{x'.";
    }
}
