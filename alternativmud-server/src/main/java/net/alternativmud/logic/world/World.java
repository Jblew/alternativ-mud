/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.world;

import net.alternativmud.logic.geo.Coordinates3l;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.lib.persistence.PersistenceManager;
import net.alternativmud.logic.User;
import net.alternativmud.logic.time.TimeMachine;
import net.alternativmud.logic.world.area.Location;
import net.alternativmud.logic.world.characters.UCharacter;
import net.alternativmud.logic.world.characters.UCharacter.Manager;
import org.joda.time.DateTimeFieldType;

/**
 *
 * @author jblew
 */
public class World {
    private final UCharacter.Manager charactersManager;
    private final Location.Manager locationsManager;
    private final TimeMachine timeMachine;
    private final Sun sun;
    private final HomePlanet homePlanet;

    public World(PersistenceManager persistenceManager, User.Manager usersManager) {
        charactersManager = new UCharacter.Manager(persistenceManager);
        locationsManager = new Location.Manager(persistenceManager);
        sun = persistenceManager.loadObject("sun", new Sun());
        timeMachine = persistenceManager.loadObject("time-machine", new TimeMachine());
        homePlanet = persistenceManager.loadObject("home-planet", new HomePlanet());
        
        Logger.getLogger(World.class.getName()).log(Level.INFO, "World loaded. Time is {0} and counting with speed factor x{1}",
                new Object[]{
                    timeMachine.getString(Coordinates3l.CENTER),
                    timeMachine.getConfig().getLocalTimestamp().getAccelerationFactor()
                });
    }

    public Manager getCharactersManager() {
        return charactersManager;
    }

    public Location.Manager getLocationsManager() {
        return locationsManager;
    }

    public TimeMachine getTimeMachine() {
        return timeMachine;
    }

    public HomePlanet getHomePlanet() {
        return homePlanet;
    }

    public Sun getSun() {
        return sun;
    }

    public void save(PersistenceManager persistenceManager) throws IOException {
        charactersManager.save(persistenceManager);
        locationsManager.save(persistenceManager);
        persistenceManager.saveObject("sun", sun);
        persistenceManager.saveObject("time-machine", timeMachine);
        persistenceManager.saveObject("home-planet", homePlanet);
        
    }
}
