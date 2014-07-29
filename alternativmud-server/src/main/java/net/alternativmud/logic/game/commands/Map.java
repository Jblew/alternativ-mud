/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.game.commands;

import net.alternativmud.App;
import net.alternativmud.logic.game.Gameplay;
import net.alternativmud.logic.world.area.Location;

/**
 *
 * @author jblew
 */
public class Map implements Command {

    @Override
    public String execute(Gameplay game, String[] parts) {
        Location l = App.getApp().getWorld().getLocationsManager().getLocation(
                game.getCharacter().getLocationCoordinates());
        return l.drawMap()+"\r\n";
    }
}
