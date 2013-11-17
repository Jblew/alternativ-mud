/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.util;

/**
 * Pomocnicze metody do rozpoznawania typów
 *
 * @author jblew
 */
public class TypeUtils {

    private TypeUtils() {
    }

    public static boolean isInteger(String in) {
        try {
            Integer.parseInt(in);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
