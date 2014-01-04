/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.framework;

import com.google.common.eventbus.EventBus;
import java.util.HashMap;
import java.util.Map;

/**
 * Klasa ServiceManager przechowuje Usługi(Services) zarejestrowane przez aplikację.
 * @author jblew
 */
public class ServiceManager {
    private final EventBus ebus;
    private Map<String, Service> services = new HashMap<>();
    
    public ServiceManager(EventBus ebus) {
        this.ebus = ebus;
    }
    
    /**
     * Zarejestruj usługę
     * @param cls - Klasa interfejsu usługi
     * @param service - Implementacja usługi
     */
    public synchronized void register(Class<? extends Service> cls, Service service) {
        services.put(cls.getName(), service);
        ebus.post(new ServiceRegisteredEvent(cls, service));
    }
    
    /**
     * Wyrejestruj usługę
     * @param cls - Klasa interfejsu usługi
     */
    public synchronized void unregister(Class<? extends Service> cls) {
        services.remove(cls.getName());
        ebus.post(new ServiceUnregisteredEvent(cls));
    }
    
    /**
     * Sprawdza czy usługa jest zarejestrowana
     * @param cls - Klasa interfejsu usługi
     * @return true if service is registered, false otherwise
     */
    public synchronized boolean isRegistered(Class<? extends Service> cls) {
        return services.containsKey(cls.getName());
    }
    
    /**
     * Pobiera implementację usługi
     * @param cls - Klasa interfejsu usługi
     * @return Implementacja usługi
     */
    public synchronized Service getService(Class<? extends Service> cls) {
        return services.get(cls.getName());
    }
    
    /**
     * Klasa nadrzędna dla Eventów Menedżera usług (ServiceManager).
     */
    public static abstract class ServiceManagerEvent {
        
    }
    
    /**
     * Wydarzenie: Zarejestrowano usługę
     */
    public static class ServiceRegisteredEvent extends ServiceManagerEvent {
        public final Class<? extends Service> serviceClass;
        public final Service implementation;
        
        public ServiceRegisteredEvent(Class<? extends Service> serviceClass, Service implementation) {
            this.serviceClass = serviceClass;
            this.implementation = implementation;
        }
        
        @Override
        public String toString() {
            return "Service registered "+this.serviceClass.getName();
        }
    }
    
    /**
     * Wydarzenie: Wyrejestrowano usługę
     */
    public static class ServiceUnregisteredEvent extends ServiceManagerEvent {
        public final Class<? extends Service> serviceClass;
        
        public ServiceUnregisteredEvent(Class<? extends Service> serviceClass) {
            this.serviceClass = serviceClass;
        }
        
        @Override
        public String toString() {
            return "Service unregistered "+this.serviceClass.getName();
        }
    }
}
