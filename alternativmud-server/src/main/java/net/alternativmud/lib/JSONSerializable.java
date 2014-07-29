/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib;

/**
 * Ten interfejs powinny implementować klasy, które mogą być
 * serializowane do formatu JSON. Klasy te powinny także
 * posiadać statyczną metodę "public static <? extends JSONSerializable> fromJSON();
 * @author jblew
 */
public interface JSONSerializable {
    public String toJSON();
    /**
     * public static <? extends JSONSerializable> fromJSON();
     * Klasy JSONSerializable powinny ją mieć.
     */
}
