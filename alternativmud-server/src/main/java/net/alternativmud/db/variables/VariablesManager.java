/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.db.variables;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author teofil
 */
public class VariablesManager {
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final VariablesConfig config;
    private final PersistenceProvider persistenceProvider;

    public VariablesManager(VariablesConfig config) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.config = config;
        persistenceProvider = (PersistenceProvider) Class.forName(config.getProviderClass()).newInstance();
    }
    
    public void init() {
        persistenceProvider.connect(config.getDriverClass(), config.getUrl());
        
        for(String key : DefaultVariables.VARIABLES.keySet()) {
            String value = DefaultVariables.VARIABLES.get(key);
            persistenceProvider.setValue(key, value);
        }
        
        initialized.set(true);
    }
    
    public String getValue(String key) {
        return persistenceProvider.getValue(key);
    }
    
    public void setValue(String key, String value){
        persistenceProvider.setValue(key, value);
    }
    
    public void close() {
        persistenceProvider.close();
        initialized.set(false);
    }
    
    public boolean isInitialized() {
        return initialized.get();
    }
    
    public static class VariablesConfig {
        private String providerClass = H2SQLPersistenceProvider.class.getName();
        private String driverClass = "org.h2.Driver";
        private String url = "jdbc:h2:mem:trialdbvariables";

        public VariablesConfig() {
        }
        
        public String getProviderClass() {
            return providerClass;
        }

        public void setProviderClass(String providerClass) {
            this.providerClass = providerClass;
        }

        public String getDriverClass() {
            return driverClass;
        }

        public void setDriverClass(String driverClass) {
            this.driverClass = driverClass;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
