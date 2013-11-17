/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.util;

import java.io.IOException;

/**
 *
 * @author jblew
 */
public interface StringWriter {
    public void print(String s) throws IOException;
    public void println(String s) throws IOException;
}
