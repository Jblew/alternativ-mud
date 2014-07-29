/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.system.tester;

/**
 * Interfejs dla testów systemowych.
 * @author jblew
 */
public interface RuntimeTest {
    public void execute() throws TestException;
    public String getName();
}
