/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.event;

import java.io.Closeable;
import java.net.InetAddress;
import net.alternativmud.lib.util.StringWriter;

/**
 * To jest klasa zbiorcza. Jej jedynym zadaniem jest zgrupowanie podklas
 * z Sieciowymi Eventami.
 * @author jblew
 */
public class NetworkEvent {
    private NetworkEvent() {}
    
    public static interface ConnectionAcceptedEvent {
        public InetAddress getAddress();
        public StringWriter getWriter();
        public Closeable getCloseable();
    }
    
    public static interface TextEvent {
        public String getText();
    }
}
