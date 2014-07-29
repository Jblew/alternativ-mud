/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.telnetserver;

/**
 * Testuje zamienianie terminalowych kolorów
 * @author jblew
 */
public class TestColor {
    public void testColorify() {
        System.out.println("colorify");
        String in = "To {rjest {gtest {bjednostkowy {xi basta {{.";
        String expResult = "To \033[0;31mjest \033[0;32mtest \033[0;34mjednostkowy \033[0mi basta {.";
        String result = Color.colorify(in);
        if(!result.equals(expResult)) throw new RuntimeException("TestColorify failed."
                + " Expected result: '"+expResult+"', got: '"+result+"'");
    }
}
