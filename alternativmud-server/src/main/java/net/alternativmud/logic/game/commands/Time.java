package net.alternativmud.logic.game.commands;

import net.alternativmud.App;
import net.alternativmud.logic.game.Gameplay;

/**
 * @author noosekpl
 */
public class Time implements Command {

    @Override
    public String execute(Gameplay game, String[] parts) {
        return "Czas(dla twojej strefy): "+ App.getApp().getWorld().getTimeMachine()
                .getString(game.getCharacter().getLocationCoordinates());
    }
}
