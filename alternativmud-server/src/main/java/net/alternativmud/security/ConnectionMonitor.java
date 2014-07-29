/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.security;

import com.google.common.eventbus.Subscribe;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.lib.event.NetworkEvent;

/**
 * Ta klasa sprawdza, czy adresy ip połączeń nie są na czarnej liście. Jeśli są, zamyka je.
 * Aby klasa ta działała, trzeba ją zarejestrować w systemowej szynie wydarzeń (EventBus).
 * @author jblew
 */
public class ConnectionMonitor {
    private static final String _BLACKLIST_URL = "http://sblam.com/blacklist.txt";
    private final List<String> ipBlacklist = Collections.synchronizedList(new ArrayList<String>());
    
    public ConnectionMonitor() {       
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.getLogger(ConnectionMonitor.class.getName()).info("Downloading ip blacklist from "+_BLACKLIST_URL+"...");
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(_BLACKLIST_URL).openStream()));
                    while(true) {
                        String line = reader.readLine();
                        if(line == null) break;
                        else if(!line.trim().startsWith("#")) addIpToBlacklist(line.trim());
                    }
                    Logger.getLogger(ConnectionMonitor.class.getName()).info("Downloading ip blacklist done.");
                } catch (IOException ex) {
                    Logger.getLogger(ConnectionMonitor.class.getName()).log(Level.SEVERE, "Cannot download ip blacklist.", ex);
                }
                
                
            }
        }, "downloading-ip-blacklist").start();*/
    }
    
    public void addIpToBlacklist(String ip) {
        ipBlacklist.add(ip);
        //App.getApp().getSystemEventBus().post(new IPAddedToBlacklistEvent(ip));
    }
    
    @Subscribe
    public void connectionAccepted(NetworkEvent.ConnectionAcceptedEvent evt) {
        if(ipBlacklist.contains(evt.getAddress().getHostAddress())) {
            try {
                evt.getWriter().println("We are sorry, but your ip address is on blacklist. Closing connection.");
                evt.getCloseable().close();
            } catch (IOException ex) {
                Logger.getLogger(ConnectionMonitor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }
    
    /*public static class IPAddedToBlacklistEvent {
        public final String ip;
        
        public IPAddedToBlacklistEvent(String ip) {
            this.ip = ip;
        }
        
        @Override
        public String toString() {
            return "IPAddedToBlacklist: "+ip;
        }
    }*/
}
