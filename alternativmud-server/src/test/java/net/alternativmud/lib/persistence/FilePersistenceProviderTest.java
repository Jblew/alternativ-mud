/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.persistence;

import java.util.ArrayList;
import junit.framework.TestCase;
import net.alternativmud.system.bootstrap.CreateFileTree;

/**
 *
 * @author jblew
 */
public class FilePersistenceProviderTest extends TestCase {
    
    public FilePersistenceProviderTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        new CreateFileTree().execute();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of saveCollection method, of class FilePersistenceProvider.
     */
    public void testSaveAndLoadCollection() throws Exception {
        ArrayList<Person> list = new ArrayList<>();
        
        Person p1 = new Person();
        p1.setEmail("harry@alternativmud.net");
        p1.setName("Harry");
        list.add(p1);
        
        Person p2 = new Person();
        p2.setEmail("sally@alternativmud.net");
        p2.setName("Sally");
        list.add(p2);
        
        FilePersistenceProvider instance = new FilePersistenceProvider();
        instance.saveCollection("unit-test-data-collection", list);
        
        ArrayList<Person> persons = instance.loadCollection("unit-test-data-collection", new Person [] {});
        boolean foundHarry = false;
        boolean foundSally = false;
        
        for(Person p : persons) {
            switch (p.getName()) {
                case "Harry":
                    assertEquals("harry@alternativmud.net", p.getEmail());
                    foundHarry = true;
                    break;
                case "Sally":
                    assertEquals("sally@alternativmud.net", p.getEmail());
                    foundSally = true;
                    break;
                default:
                    fail("Loaded more records than were saved!");
                    break;
            }
        }
        
        assertTrue("Did not found Harry", foundHarry);
        assertTrue("Did not found Sally", foundSally);
    }
    
    public void testSaveAndLoadSingleObject() throws Exception {
        
        Person p = new Person();
        p.setEmail("harry@alternativmud.net");
        p.setName("Harry");
        
        FilePersistenceProvider instance = new FilePersistenceProvider();
        instance.saveObject("unit-test-data-single", p);
        
        Person found = instance.loadObject("unit-test-data-single", new Person());
        boolean foundHarry = false;
        
        assertNotNull("Loaded object is null", found);
        
        assertEquals("Person name does not match", p.getName(), found.getName());
        assertEquals("Person email does not match", p.getEmail(), found.getEmail());
    }
    
    private static class Person {
        private String name;
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Person{" + "name=" + name + ", email=" + email + '}';
        }
    }
}
