/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.db.DBEntitiesManager.DatabaseConfig;
import net.alternativmud.db.variables.VariablesManager;
import net.alternativmud.logic.time.TimeConfig;
import net.alternativmud.system.web.WebServer;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 *
 * @author teofil
 */

public class Config {
    private long tcpEBusTimeoutMs = 1/*m*/*60/*s*/*1000/*ms*/;
    private DatabaseConfig databaseConfig = new DatabaseConfig();
    private WebServer.WebServerConfig webServerConfig = new WebServer.WebServerConfig();
    private VariablesManager.VariablesConfig variablesConfig = new VariablesManager.VariablesConfig();
    private String httpSessionSalt = "f3g45egrv34%#$%G#vrtfdsv^R$brrbTTYGT135";
    private TimeConfig timeConfig = new TimeConfig();

    public long getTcpEBusTimeoutMs() {
        return tcpEBusTimeoutMs;
    }

    public void setTcpEBusTimeoutMs(long tcpEBusTimeoutMs) {
        this.tcpEBusTimeoutMs = tcpEBusTimeoutMs;
    }

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }

    public void setDatabaseConfig(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }
    
    public WebServer.WebServerConfig getWebServerConfig() {
        return webServerConfig;
    }

    public void setWebServerConfig(WebServer.WebServerConfig webServerConfig) {
        this.webServerConfig = webServerConfig;
    }

    public VariablesManager.VariablesConfig getVariablesConfig() {
        return variablesConfig;
    }

    public void setVariablesConfig(VariablesManager.VariablesConfig variablesConfig) {
        this.variablesConfig = variablesConfig;
    }
    
    public String getHttpSessionSalt() {
        return httpSessionSalt;
    }

    public void setHttpSessionSalt(String httpSessionSalt) {
        this.httpSessionSalt = httpSessionSalt;
    }

    public TimeConfig getTimeConfig() {
        return timeConfig;
    }

    public void setTimeConfig(TimeConfig timeConfig) {
        this.timeConfig = timeConfig;
    }
    
    /**
     * Following methods should be used for loading and saving config.
     */
    @JsonIgnore
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    static {
        JSON_MAPPER.setSerializationConfig(JSON_MAPPER.getSerializationConfig()
                .without(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS))
                .enable(SerializationConfig.Feature.INDENT_OUTPUT);
    }
    
    public void save(String configFile) {
        try {
            JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValue(new File(configFile), this);
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, "Could not save config to file "+configFile, ex);
        }
    }
    
    public static Config LoadConfig(String configFile) {
        try {
            return JSON_MAPPER.readValue(new File(configFile), Config.class);
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, "Could not load config from file "+configFile+", loading default config", ex);
        }
        return new Config();
    }
}
