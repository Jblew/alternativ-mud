/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.util;

import java.util.List;
import java.util.Map;

/**
 * Metody użyteczne przy debugowaniu
 * @author jblew
 */
public class DebugUtils {
    private DebugUtils() {}
    
    public static String mapToString(Map m) {
        String out = "";
        for(Object k : m.keySet()) {
            out += "...["+k.toString()+"] = "+m.toString()+"\n";
        }
        return out;
    }
    
    public static String listToString(List l) {
        String out = "";
        for(Object elem : l) {
            out += "..."+l+"\n";
        }
        return out;
    }
    
    public static String arrayToString(Object [] arr) {
        String out = arr.getClass().getName()+" [] {";
        for(Object elem : arr) {
            out += elem+", ";
        }
        return out+"}";
    }
    
    public static String arrayToString(double [] arr) {
        String out = "double [] {";
        for(Object elem : arr) {
            out += elem+", ";
        }
        return out+"}";
    }
    
    public static String arrayToString(int [] arr) {
        String out = "int [] {";
        for(Object elem : arr) {
            out += elem+", ";
        }
        return out+"}";
    }
    
    public static String arrayToString(float [] arr) {
        String out = "float [] {";
        for(Object elem : arr) {
            out += elem+", ";
        }
        return out+"}";
    }
    
    public static String arrayToString(long [] arr) {
        String out = "long [] {";
        for(Object elem : arr) {
            out += elem+", ";
        }
        return out+"}";
    }
    
    public static String arrayToString(short [] arr) {
        String out = "short [] {";
        for(Object elem : arr) {
            out += elem+", ";
        }
        return out+"}";
    }
    
    public static String arrayToString(boolean [] arr) {
        String out = "boolean [] {";
        for(Object elem : arr) {
            out += elem+", ";
        }
        return out+"}";
    }
}
