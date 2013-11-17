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
        User.Manager usersMgr = new User.Manager(persistanceManager);
        //BasicDBObject query = new BasicDBObject();
        //query.put("login", "tester");
        assertNotNull("User tester does not exist", usersMgr.getUser("tester"));
        
        User u = usersMgr.authenticate("tester", "tester");
        assertNotNull("Cannot authenticate tester", u);
        assertEquals("Got different user than tester.", "tester", u.getLogin());
    }
}
