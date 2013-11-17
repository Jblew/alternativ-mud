/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.gclient.actions;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.gclient.App;
import net.alternativmud.gclient.forms.*;
import net.alternativmud.lib.debug.BusLogger;
import net.alternativmud.system.nebus.server.NetworkBusClient;
import net.alternativmud.system.nebus.server.StandardBusSubscriber;

/**
 *
 * @author jblew
 */
public class Connect implements Runnable {
    private final ExecutorService executor;
    private final FormFrame formFrame;
    public Connect(ExecutorService executor, FormFrame formFrame) {
        this.executor = executor;
        this.formFrame = formFrame;
    }

    @Override
    public void run() {
        final ConnectForm f = new ConnectForm();
        formFrame.setForm(f);
        f.getConnectButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        String host = f.getHostField().getText();
                        int port = (Integer) f.getPortField().getValue();
                        try {
                            f.setForeground(Color.BLUE);
                            f.getMessageLabel().setText("Connecting...");
                            NetworkBusClient clientBus = new NetworkBusClient(new InetSocketAddress(host, port));
                            clientBus.register(new BusLogger());
                            f.setForeground(Color.GREEN);
                            f.getMessageLabel().setText("Connected");
                            
                            executor.execute(new Login(executor, formFrame, clientBus));
                        } catch (IOException ex) {
                            f.getMessageLabel().setForeground(Color.RED);
                            f.getMessageLabel().setText("Cannot connect: " + ex.getMessage());
                            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                });
            }

        });
    }

}
