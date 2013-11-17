/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.util;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Random;

/**
 * Różne narzędzia sieciowe
 *
 * @author jblew
 */
public class NetworkUtils {

    private NetworkUtils() {
    }

    /**
     * Sprawdza, czy port jest wolny.
     *
     * @param port
     */
    public static boolean isAvailable(int port) {
        if (port < 0 || port > 65536) {
            throw new IllegalArgumentException("Invalid port: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /*
                     * should not be thrown
                     */
                }
            }
        }

        return false;
    }
    
    private static final Random rnd = new Random();
    public static int getRandomAvailablePort(int from, int to) {
        int port = -1;
        for(int i = 0;i < 1500;i++) {
            port = from+rnd.nextInt(to);
            if(isAvailable(port)) {
                break;
            }
        }
        if(port == -1) throw new RuntimeException("Checked 1500 ports, none of them is available.");
        return port;
    }
}
