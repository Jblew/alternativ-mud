/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system;

import net.alternativmud.StaticConfig;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import net.alternativmud.lib.annotation.Enity;

/**
 *
 * @author maciek
 */
public class StaticConfigTest extends TestCase {
    
    public StaticConfigTest(String testName) {
        super(testName);
    }

    public void testIfEnityClassesExists() {
        String doesNotExist = "";
        for(String clsName : StaticConfig.ENITY_CLASSES) {
            try {
                Class.forName(clsName);
            } catch (ClassNotFoundException ex) {
                doesNotExist += clsName+";";
            }
        }
        
        if(!doesNotExist.isEmpty()) fail("The following enityClasses could not be found: "+doesNotExist);
    }
    
    public void testIfEnityClassesAreAnnotatedWithEnityAnnotation() {
        for(String clsName : StaticConfig.ENITY_CLASSES) {
            try {
                if(!Class.forName(clsName).isAnnotationPresent(Enity.class)) {
                    fail("Class "+clsName+" does not have @Enity.");
                }
            } catch (ClassNotFoundException ex) {
            }
        }
    }
}
