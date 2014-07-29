/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud;

/**
 *
 * @author jblew
 */
public class GeoConfig {
    private GeoConfig() {}
    
    public static final int LOCATIONS_IN_QUARTER = 90;//1 lokacja na stopien. cwiartka = 90
    public static final double ANGLE_UNIT = 90/LOCATIONS_IN_QUARTER;//degree
    
    public static final int STANDARD_RADIUS = 100;//locations
    
    public static final double DISTANCE_UNIT = 5; //meters
    
    public static final double TROPIC_LATITUDE_CIRCLE_FRACTION = 23.4500444/360;//fraction
}
