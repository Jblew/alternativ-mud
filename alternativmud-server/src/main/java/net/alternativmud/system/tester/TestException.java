/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.system.tester;

/**
 * Ten wyjątek powinien być zgłaszany, jeżeli test systemowy zawiedzie.
 * @author jblew
 */
public class TestException extends Exception {
    private final RuntimeTest test;

    public TestException(RuntimeTest test, String msg) {
        super(msg);
        this.test = test;
    }

    public TestException(RuntimeTest test, Exception e) {
        super(e);
        this.test = test;
    }
}
