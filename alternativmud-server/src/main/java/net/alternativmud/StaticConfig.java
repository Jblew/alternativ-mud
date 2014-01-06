/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud;

/**
 * Tutaj umieszczamy statyczną konfigurację, czyli np. nazw pliku bazy danych.
 * @author jblew
 */
public class StaticConfig {
    public static final long TCP_EBUS_TIMEOUT_MS = 1/*m*/*60/*s*/*1000/*ms*/;
    private StaticConfig() {}
        
    public static final String [] ENITY_CLASSES = new String [] {
        net.alternativmud.logic.User.class.getName()
    };
    
    public static final String [] NTP_SERVERS = new String [] {
        "tempus1.gum.gov.pl",
        "tempus2.gum.gov.pl"
    };
}
