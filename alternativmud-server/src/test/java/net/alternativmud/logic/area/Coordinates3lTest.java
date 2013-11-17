/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.area;

import junit.framework.TestCase;
import net.alternativmud.GeoConfig;
import net.alternativmud.logic.geo.Coordinates3l;

/**
 *
 * @author jblew
 */
public class Coordinates3lTest extends TestCase {
    
    public Coordinates3lTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void testIfGetNorthernProperlyPasesNorthPole() {
        Coordinates3l northPole = new Coordinates3l(0, GeoConfig.LOCATIONS_IN_QUARTER);
        Coordinates3l furtherThanNorth = northPole.getNorthern();
        assertEquals("Radius doesn't equal", northPole.getRadius(), furtherThanNorth.getRadius());
        assertEquals("Latitude does not equal", northPole.getLatitude(), furtherThanNorth.getLatitude());
        assertEquals("Longitude is not opposite", (GeoConfig.LOCATIONS_IN_QUARTER*2), furtherThanNorth.getLongitude());
    }
    
    public void testIfGetNorthernProperlyPasesSouthPole() {
        Coordinates3l southPole = new Coordinates3l(0, -GeoConfig.LOCATIONS_IN_QUARTER);
        Coordinates3l furtherThanNorth = southPole.getNorthern();
        assertEquals("Radius doesn't equal", southPole.getRadius(), furtherThanNorth.getRadius());
        assertEquals("Latitude is not greater by one", southPole.getLatitude()+1, furtherThanNorth.getLatitude());
        assertEquals("Longitude does not equal", southPole.getLongitude(), furtherThanNorth.getLongitude());
    }
    
    public void testIfGetNorthernProperlyPasesEquator() {
        Coordinates3l beforeEquator = new Coordinates3l(0, -1);
        Coordinates3l northern = beforeEquator.getNorthern();
        assertEquals("Radius doesn't equal", beforeEquator.getRadius(), northern.getRadius());
        assertEquals("Latitude is not greater by one", beforeEquator.getLatitude()+1, northern.getLatitude());
        assertEquals("Longitude does not equal", beforeEquator.getLongitude(), northern.getLongitude());
    }
    
    public void testIfGetSouthernProperlyPasesNorthPole() {
        Coordinates3l northPole = new Coordinates3l(0, GeoConfig.LOCATIONS_IN_QUARTER);
        Coordinates3l southern = northPole.getSouthern();
        assertEquals("Radius doesn't equal", northPole.getRadius(), southern.getRadius());
        assertEquals("Latitude is not lower by one", northPole.getLatitude()-1, southern.getLatitude());
        assertEquals("Longitude is not equal", northPole.getLongitude(), southern.getLongitude());
    }
    
    public void testIfGetSouthernProperlyPasesSouthPole() {
        Coordinates3l southPole = new Coordinates3l(0, -GeoConfig.LOCATIONS_IN_QUARTER);
        Coordinates3l furtherThanSouth = southPole.getSouthern();
        assertEquals("Radius doesn't equal", southPole.getRadius(), furtherThanSouth.getRadius());
        assertEquals("Latitude does not equal", southPole.getLatitude(), furtherThanSouth.getLatitude());
        assertEquals("Longitude is not opposite", GeoConfig.LOCATIONS_IN_QUARTER*2, furtherThanSouth.getLongitude());
    }
    
    public void testIfGetSouthernProperlyPasesEquator() {
        Coordinates3l beforeEquator = new Coordinates3l(0, 1);
        Coordinates3l southern = beforeEquator.getSouthern();
        assertEquals("Radius doesn't equal", beforeEquator.getRadius(), southern.getRadius());
        assertEquals("Latitude is not lower by one", beforeEquator.getLatitude()-1, southern.getLatitude());
        assertEquals("Longitude does not equal", beforeEquator.getLongitude(), southern.getLongitude());
    }
    
    public void testIfGetWesternProperlyPasesPrimeMeridian() {
        Coordinates3l beforePrimeMeridian = new Coordinates3l(0, 1);
        Coordinates3l western = beforePrimeMeridian.getWestern();
        assertEquals("Radius doesn't equal", beforePrimeMeridian.getRadius(), western.getRadius());
        assertEquals("Latitude is not equal", beforePrimeMeridian.getLatitude(), western.getLatitude());
        assertEquals("Longitude is not "+(GeoConfig.LOCATIONS_IN_QUARTER*4-1), (GeoConfig.LOCATIONS_IN_QUARTER*4-1), western.getLongitude());
    }
    
    public void testIfGetEasternProperlyPasesPrimeMeridian() {
        Coordinates3l beforePrimeMeridian = new Coordinates3l((GeoConfig.LOCATIONS_IN_QUARTER*4-1), 1);
        Coordinates3l eastern = beforePrimeMeridian.getEastern();
        assertEquals("Radius doesn't equal", beforePrimeMeridian.getRadius(), eastern.getRadius());
        assertEquals("Latitude is not equal", beforePrimeMeridian.getLatitude(), eastern.getLatitude());
        assertEquals("Longitude is not 0", 0, eastern.getLongitude());
    }

    public void testIfGetLowerStopsAtGlobeCenter() {
        Coordinates3l one = new Coordinates3l(1, 0, 0);
        Coordinates3l lower = one.getLower();
        assertEquals("Radius doesn't equal", one.getRadius(), lower.getRadius());
        assertEquals("Latitude is not equal", one.getLatitude(), lower.getLatitude());
        assertEquals("Longitude is not equal", 0, lower.getLongitude());
    }
    
    public void testIfConstructorThrowsExceptionForLongitudeLowerThan0() {
        try {
            new Coordinates3l(-1, 1);
            fail("Should throw IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
            
        }
    }
    
    public void testIfConstructorThrowsExceptionForLongitudeGreaterThanFourQuartersMinusOne() {
        try {
            new Coordinates3l(GeoConfig.LOCATIONS_IN_QUARTER*4, 1);
            fail("Should throw IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
            
        }
    }
    
    public void testIfConstructorThrowsExceptionForNegativeOrZeroRadius() {
        try {
            new Coordinates3l(-1, 1, 1);
            fail("Should throw IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
            
        }
        try {
            new Coordinates3l(0, 1, 1);
            fail("Should throw IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
            
        }
    }
    
    public void testIfConstructorThrowsExceptionForLatitudeGreaterThanQuarter() {
        try {
            new Coordinates3l(1, GeoConfig.LOCATIONS_IN_QUARTER+1);
            fail("Should throw IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
            
        }
    }
    public void testIfConstructorThrowsExceptionForLatitudeGreaterThanMinusQuarter() {
        try {
            new Coordinates3l(1, -GeoConfig.LOCATIONS_IN_QUARTER-1);
            fail("Should throw IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
            
        }
    }
}
