/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib;

import java.util.concurrent.ThreadFactory;

/**
 * ThreadFactory, które nazywa wątki wg. podanej nazwy, dodając na końcu
 * unikatowy numer z IDGeneratora.
 * @author jblew
 */
public class NamingThreadFactory implements ThreadFactory {
    private final String name;
    
    public NamingThreadFactory(String name) {
        this.name = name;
    }
    
    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, name+"-"+IdManager.getSessionSafe());
    }
    
}
