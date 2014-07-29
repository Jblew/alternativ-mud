/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.lib;

import java.text.SimpleDateFormat;

/**
 * Ta klasa zawiera użyteczne Formattery Daty
 * @author jblew
 */
public class DateFormatters {
    public static final SimpleDateFormat URLSAFE_DATE_FORMATTER = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss");

    private DateFormatters() {}
}
