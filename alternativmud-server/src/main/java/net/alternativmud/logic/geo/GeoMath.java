/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.geo;

import net.alternativmud.logic.geo.Coordinates3l;
import net.alternativmud.logic.time.TimeMachine;
import org.joda.time.DateTimeFieldType;

/**
 *
 * @author jblew
 */
public class GeoMath {
    private GeoMath() {}
    
    public static float getPlanetRotationFraction(TimeMachine tm) {
        return tm.get(DateTimeFieldType.secondOfDay())/24*60*60;
    }
    
    public static float getCulminationSunHeightOnEquinoxAsCircleFraction(Coordinates3l coordinates) {
        return (float) (0.25 - coordinates.getLatitudeCircleFraction());
    }
    
    public static float getCulminationSunHeightOnWinterSolsticeAsCircleFraction(Coordinates3l coordinates) {
        return (float) (0.25 - coordinates.getLatitudeInDegree());
    }
    
    public static float getSunHeight() {
        return 0;
    }
}
