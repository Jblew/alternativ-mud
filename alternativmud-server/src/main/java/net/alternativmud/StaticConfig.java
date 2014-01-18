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
    private StaticConfig() {}
        
    public static final String [] ENITY_CLASSES = new String [] {
        net.alternativmud.logic.User.class.getName()
    };
    
    public static final String DEFAULT_CONFIG_FILE = "config.json";
    
    public static final String ALTERNATIV_MUD_WEBSITE = "http://github.com/Jblew/alternativ-mud";
}
