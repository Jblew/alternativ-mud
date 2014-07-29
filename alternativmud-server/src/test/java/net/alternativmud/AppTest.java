package net.alternativmud;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        assertTrue(true);
    }

    /*
     * public void testIfAppBootstrapsCorrectly() { int i = 0; while (true) {
     * try { TimeUnit.MILLISECONDS.sleep(100); } catch (InterruptedException ex)
     * { } if (EmbeddedApp.getApp().getLifecycle().getState() !=
     * Lifecycle.State.OFF) { break; } else if (i > 150) { fail("Bootstrap init
     * timeout: more than 5s"); } i++; } if
     * (EmbeddedApp.getApp().getLifecycle().getState() == Lifecycle.State.OFF) {
     * fail("App has not started bootstrapping."); } i = 0; while (true) { try {
     * TimeUnit.MILLISECONDS.sleep(100); } catch (InterruptedException ex) { }
     * if (EmbeddedApp.getApp().getLifecycle().getState() !=
     * Lifecycle.State.BOOTSTRAP) { break; } else if (i > 150) { fail("Bootstrap
     * timeout: more than 15s"); } i++; } if
     * (EmbeddedApp.getApp().getLifecycle().getState() != Lifecycle.State.ON) {
     * fail("Lifecycle.State = " +
     * EmbeddedApp.getApp().getLifecycle().getState() + ". App has not
     * bootstrapped properly."); }
     *
     * }
     */
}
