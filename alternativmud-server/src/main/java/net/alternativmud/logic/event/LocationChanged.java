/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.event;

import net.alternativmud.logic.world.area.Location;

/**
 *
 * @author jblew
 */
public class LocationChanged {
    private Location location;
    public LocationChanged(Location location) {
        this.location = location;
    }
    public LocationChanged() {
    }
    
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
