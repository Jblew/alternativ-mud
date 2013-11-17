/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.security;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author maciek
 */
public class TestPasswordHasher {
    
    public TestPasswordHasher() {
    }

    public void testIfAlgorithmExists() {
        System.out.println("Test if MD algorithm exists.");
        assert PasswordHasher.algorithmExists();
    }
    
    public void testIfHashingWorks() {
        System.out.println("Test if hashing works.");
        boolean failed = false;
        try {
            PasswordHasher.init();
            assert PasswordHasher.generateHash("test").equals("a94a8fe5ccb19ba61c4c0873d391e987982fbbd3");
        } catch (NoSuchAlgorithmException ex) {
            failed = true;
        }
        assert !failed;
    }
}
