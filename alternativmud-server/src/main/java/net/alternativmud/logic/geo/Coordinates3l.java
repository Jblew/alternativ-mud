/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.geo;

import java.util.logging.Logger;
import net.alternativmud.GeoConfig;
import net.alternativmud.lib.containers.TwoTuple;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * TODO: Should return null for errornomous coordinates.
 * TODO: Koordynaty powinny byc kompatybilne z systemem GPS, czyli dlugosc powinna byc w przedziale <-180; 180>.
 * @author jblew
 */
public class Coordinates3l {
    public static final Coordinates3l CENTER = new Coordinates3l(180, 0);
    private final int radius;
    private final int latitude;
    private final int longitude;

    @JsonCreator
    public Coordinates3l(@JsonProperty("radius") int radius,
            @JsonProperty("longitude") int longitude,
            @JsonProperty("latitude") int latitude) {
        this.radius = radius;
        if (radius < 1) {
            throw new IllegalArgumentException("Radius have to be at least 1.");
        }

        this.longitude = longitude;
        if (longitude < 0  || longitude > GeoConfig.LOCATIONS_IN_QUARTER * 4 - 1) {
            throw new IllegalArgumentException("Longitude have to be in range:"
                    + "<0, " + GeoConfig.LOCATIONS_IN_QUARTER * 4 + ").");
        }

        this.latitude = latitude;
        if (latitude > GeoConfig.LOCATIONS_IN_QUARTER
                || latitude < -GeoConfig.LOCATIONS_IN_QUARTER) {
            throw new IllegalArgumentException("Latitude have to be in range:"
                    + "<-" + GeoConfig.LOCATIONS_IN_QUARTER + ", "
                    + "+" + GeoConfig.LOCATIONS_IN_QUARTER + ">.");
        }
    }

    /**
     * Here radius is set to GeoConfig.STANDARD_RADIUS
     */
    public Coordinates3l(int longitude, int latitude) {
        this(GeoConfig.STANDARD_RADIUS, longitude, latitude);
    }

    public int getRadius() {
        return radius;
    }

    public int getLatitude() {
        return latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    @JsonIgnore
    public Coordinates3l getNorthern() {
        int newLatitude = latitude + 1;
        int newLongitude = longitude;
        if (newLatitude > GeoConfig.LOCATIONS_IN_QUARTER) {
            newLatitude = latitude;
            newLongitude = (longitude + GeoConfig.LOCATIONS_IN_QUARTER * 2 > (GeoConfig.LOCATIONS_IN_QUARTER * 4 - 1)
                    ? longitude + GeoConfig.LOCATIONS_IN_QUARTER * 2 - (GeoConfig.LOCATIONS_IN_QUARTER * 4 - 1)
                    : longitude + GeoConfig.LOCATIONS_IN_QUARTER * 2);//180 degree
        }
        return new Coordinates3l(radius, newLongitude, newLatitude);
    }

    @JsonIgnore
    public Coordinates3l getSouthern() {
        int newLatitude = latitude - 1;
        int newLongitude = longitude;
        if (newLatitude < -GeoConfig.LOCATIONS_IN_QUARTER) {
            newLatitude = latitude;
            newLongitude = (longitude + GeoConfig.LOCATIONS_IN_QUARTER * 2 > (GeoConfig.LOCATIONS_IN_QUARTER * 4 - 1)
                    ? longitude + GeoConfig.LOCATIONS_IN_QUARTER * 2 - (GeoConfig.LOCATIONS_IN_QUARTER * 4 - 1)
                    : longitude + GeoConfig.LOCATIONS_IN_QUARTER * 2);//180 degree
        }
        return new Coordinates3l(radius, newLongitude, newLatitude);
    }

    @JsonIgnore
    public Coordinates3l getEastern() {
        return new Coordinates3l(
                radius,
                (longitude + 1 > (GeoConfig.LOCATIONS_IN_QUARTER * 4 - 1) ? 0 : longitude + 1),
                latitude);
    }

    @JsonIgnore
    public Coordinates3l getWestern() {
        return new Coordinates3l(
                radius,
                (longitude - 1 < 0 ? (GeoConfig.LOCATIONS_IN_QUARTER * 4 - 1) : longitude - 1),
                latitude);
    }

    @JsonIgnore
    public Coordinates3l getUpper() {
        return new Coordinates3l(
                radius + 1,
                longitude,
                latitude);
    }

    @JsonIgnore
    public Coordinates3l getLower() {
        return new Coordinates3l(
                (radius - 1 < 1) ? 1 : radius - 1,
                longitude,
                latitude);
    }

    @JsonIgnore
    public Coordinates3l getDirection(Direction d) {
        switch (d) {
            case NORTH:
                return getNorthern();
            case SOUTH:
                return getSouthern();
            case WEST:
                return getWestern();
            case EAST:
                return getEastern();
            case UP:
                return getUpper();
            case DOWN:
                return getLower();
            default:
                Logger.getLogger(Coordinates3l.class.getName()).warning("Coordinates3l.getDirection() does not cover all directions!");
                return this;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Coordinates3l other = (Coordinates3l) obj;
        if (this.radius != other.radius) {
            return false;
        }
        if (this.latitude != other.latitude) {
            return false;
        }
        if (this.longitude != other.longitude) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + this.radius;
        hash = 67 * hash + this.latitude;
        hash = 67 * hash + this.longitude;
        return hash;
    }

    @Override
    public String toString() {
        return "[" + radius + ", " + latitude + ", " + longitude + "]";
    }

    @JsonIgnore
    public double getLatitudeInDegree() {
        return getLatitude()*GeoConfig.ANGLE_UNIT;
    }
    
    @JsonIgnore
    public double getLongitudeInDegree() {
        return getLongitude()*GeoConfig.ANGLE_UNIT;
    }
    
    @JsonIgnore
    public double getRadiusInMeters() {
        return getRadius()*GeoConfig.DISTANCE_UNIT;
    }
    
    @JsonIgnore
    public float getLatitudeCircleFraction() {
        return getLatitude()/(4*GeoConfig.LOCATIONS_IN_QUARTER);
    }
    
    @JsonIgnore
    public float getLongitudeCircleFraction() {
        return getLongitude()/(4*GeoConfig.LOCATIONS_IN_QUARTER);
    }
    
    @JsonIgnore
    public GpsCoordinates getGpsCoordinates() {
        return new GpsCoordinates(
                latitude*(90/GeoConfig.LOCATIONS_IN_QUARTER),
                (longitude-180)*(90/GeoConfig.LOCATIONS_IN_QUARTER)
                );
    }
    
    public static class GpsCoordinates {
        private final double latitude;
        private final double longitude;
        @JsonCreator
        public GpsCoordinates(@JsonProperty("latitude") double latitude, @JsonProperty("longitude") double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
        
        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final GpsCoordinates other = (GpsCoordinates) obj;
            if (Double.doubleToLongBits(this.latitude) != Double.doubleToLongBits(other.latitude)) {
                return false;
            }
            if (Double.doubleToLongBits(this.longitude) != Double.doubleToLongBits(other.longitude)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            return hash;
        }
        
        @Override
        public String toString() {
            return "GpsCoordinates{" + "latitude=" + latitude + ", longitude=" + longitude + '}';
        }
    }
}
