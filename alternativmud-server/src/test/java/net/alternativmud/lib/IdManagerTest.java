/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib;

import junit.framework.TestCase;

/**
 *
 * @author jblew
 */
public class IdManagerTest extends TestCase {
    //private DatabaseManager dbManager = null;
    private IdManager idManager;
    
    public IdManagerTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        //dbManager = new DatabaseManager("alternativ-mud-test");
        //dbManager.bootstrap();
        idManager = new IdManager();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        //dbManager.shutdown();
    }

    public void testGetSessionSafe() {
        long n1 = IdManager.getSessionSafe();
        long n2 = IdManager.getSessionSafe();
        assertEquals("IdManager.getSessionSafe does nt generate sequential numbers!",n2, n1+1);
    }
}
