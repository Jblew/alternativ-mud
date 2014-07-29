/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.system.unityserver;

import java.net.InetAddress;
import java.util.Objects;

/**
 *
 * @author teofil
 */
public class UDPAddressTuple {
    private final InetAddress inetAddress;
    private final int port;

    public UDPAddressTuple(InetAddress inetAddress, int port) {
        this.inetAddress = inetAddress;
        this.port = port;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "UDPAddressTuple{" + "inetAddress=" + inetAddress + ", port=" + port + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.inetAddress);
        hash = 53 * hash + this.port;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UDPAddressTuple other = (UDPAddressTuple) obj;
        if (!Objects.equals(this.inetAddress, other.inetAddress)) {
            return false;
        }
        if (this.port != other.port) {
            return false;
        }
        return true;
    }
    
    
}
