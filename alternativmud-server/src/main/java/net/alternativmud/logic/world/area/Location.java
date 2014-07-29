/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.world.area;

import net.alternativmud.logic.geo.Coordinates3l;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.App;
import net.alternativmud.GeoConfig;
import net.alternativmud.lib.persistence.PersistenceManager;
import net.alternativmud.logic.game.Gameplay;
import net.alternativmud.logic.geo.Direction;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Opis moznaby zastąpić zestawem cech potrzebnych do jego generowania.
 *
 * @author jblew
 */
public class Location {
    private Coordinates3l coordinates = new Coordinates3l(0, 0);
    private Base base = Base.GRASS;
    private Atmosphere atmosphere = Atmosphere.CLEAR_AIR;
    private Environment environment = Environment.SURFACE_OUTDOOR;
    private String name = "";
    private String sign = "x";
    private String description = "";

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Coordinates3l getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates3l location) {
        this.coordinates = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
    public Atmosphere getAtmosphere() {
        return atmosphere;
    }

    public void setAtmosphere(Atmosphere atmosphere) {
        this.atmosphere = atmosphere;
    }

    public Base getBase() {
        return base;
    }

    public void setBase(Base base) {
        this.base = base;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @JsonIgnore
    public String getLook(Gameplay game) {
        String out = "{C" + getName() + "{x ";

        out += " {c[";
        boolean exits = false;
        Location.Manager mgr = App.getApp().getWorld().getLocationsManager();
        for (Direction d : Direction.values()) {
            Coordinates3l c = getCoordinates().getDirection(d);
            Location l = mgr.getLocation(c);
            if (l != null) {
                exits = true;
                out += " " + d.name().toLowerCase();
            }
        }

        if (!exits) {
            out += " brak wyjść";
        }
        out += " ]{x\r\n";

        out += getDescription() + "\r\n";

        for (Gameplay gplay : App.getApp().getGamesManager().getGameplays()) {
            if (gplay == game) {
                continue;
            }

            if (gplay.getCharacter()
                    .getLocationCoordinates()
                    .equals(
                    game
                    .getCharacter()
                    .getLocationCoordinates())) {
                out += "   {C" + gplay.getCharacter().getName() + "{x\r\n";
            }
        }

        return out;
    }

    @JsonIgnore
    public String drawMap() {
        String out = "";

        int mapXSize = 10;
        int mapYSize = 10;

        int centerX = mapXSize / 2;
        int centerY = mapYSize / 2;

        Location.Manager mgr = App.getApp().getWorld().getLocationsManager();

        for (int y = centerY; y > -centerY; y--) {
            for (int x = -centerX; x < centerX; x++) {
                int radius = getCoordinates().getRadius();
                int longitude = getCoordinates().getLongitude() + x;
                int latitude = getCoordinates().getLatitude() + y;
                if (longitude >= 0 && longitude <= GeoConfig.LOCATIONS_IN_QUARTER * 4 - 1
                        && latitude <= GeoConfig.LOCATIONS_IN_QUARTER
                        && latitude >= -GeoConfig.LOCATIONS_IN_QUARTER) {
                    Location l = mgr.getLocation(new Coordinates3l(
                            radius,
                            longitude,
                            latitude));
                    if (l == null) {
                        out += " ";
                    } else if (l == this) {
                        out += "{C0{x";
                    } else {
                        out += l.getSign();
                    }
                } else {
                    out += " ";
                }
            }
            out += "\r\n";
        }

        return out;
    }
    
    public static Location build(Coordinates3l coordinates) {
        Location l = new Location();
        l.setCoordinates(coordinates);
        return l;
    }

    public static class Manager {

        private final ArrayList<Location> list = new ArrayList<>();

        public Manager(PersistenceManager persistenceManager) {
            try {
                list.addAll(persistenceManager.loadCollection("locations", new Location[]{}));
            } catch (IOException ex) {
                Logger.getLogger(Location.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (list.size() < 1) {
                Location l1 = new Location();
                l1.setName("The heart of the World");
                l1.setDescription("It's huge green field. The weather is sunny, "
                        + "but light does not blind you. You feel happy.");
                l1.setCoordinates(new Coordinates3l(0, 0));
                list.add(l1);

                Location l2 = new Location();
                l2.setName("Northern land");
                l2.setDescription("You are standing in the front of huge ice wall."
                        + " It's a northern border of this land.");
                l2.setCoordinates(new Coordinates3l(0, 0).getNorthern());
                list.add(l2);

                Location l3 = new Location();
                l3.setName("Western land");
                l3.setDescription("You looks on the west, and sees endless, "
                        + "sandy desert. This desert marks the western border"
                        + "of this country.");
                l3.setCoordinates(new Coordinates3l(0, 0).getSouthern());
                list.add(l3);

                Location l4 = new Location();
                l4.setName("North-eastern land");
                l4.setDescription("Lorem ipsum dolor sit amet.");
                l4.setCoordinates(l2.getCoordinates().getEastern());
                list.add(l4);
            }
        }

        public Location getLocation(Coordinates3l coordinates) {
            for (Location r : list) {
                if (r.getCoordinates().equals(coordinates)) {
                    return r;
                }
            }
            Location l = new Location();
            l.setName("Location " + coordinates.toString());
            l.setDescription("Nice description...");
            l.setSign("+");
            l.setCoordinates(coordinates);
            list.add(l);
            return l;
        }

        public List<Location> getLocations() {
            return list;
        }

        public void save(PersistenceManager persistenceManager) throws IOException {
            persistenceManager.saveCollection("locations", list);
        }
    }
}
