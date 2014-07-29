/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic;

import junit.framework.TestCase;
import net.alternativmud.lib.persistence.NullPersistenceProvider;
import net.alternativmud.lib.persistence.PersistenceManager;

/**
 *
 * @author jblew
 */
public class UserTest extends TestCase {
    private PersistenceManager persistanceManager;
    
    public UserTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        persistanceManager = new PersistenceManager(new NullPersistenceProvider());
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testTesterAuthentication() {
        User u = new User();
        u.setLogin("tester");
        u.setPassword("tester");
        
        assertTrue("Checking password", u.isPasswordCorrect("tester"));
        assertFalse("Checking password", u.isPasswordCorrect("tester1"));
        assertEquals("Got different user than tester.", "tester", u.getLogin());
    }
}
