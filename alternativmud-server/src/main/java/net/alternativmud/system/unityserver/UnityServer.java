/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.unityserver;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.App;
import net.alternativmud.framework.ExternalService;
import net.alternativmud.game3d.UnityScenes;
import net.alternativmud.lib.NamingThreadFactory;

/**
 *
 * @author Jedrzej Lewandowski
 */
public class UnityServer implements ExternalService {
    private final EventBus eBus;
    private final SceneDataManager[] sceneDataManagers = new SceneDataManager[UnityScenes.SCENES.length];
    private final ExecutorService sceneExecutors = Executors.newFixedThreadPool(UnityScenes.SCENES.length, new NamingThreadFactory("unity-cene-server"));//unique thread for every scene
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(true);
    private AtomicInteger receivedPackets = new AtomicInteger(0);
    private AtomicInteger sentPackets = new AtomicInteger(0);

    public UnityServer(EventBus eBus) {
        this.eBus = eBus;
        
        for (byte i = 0; i < sceneDataManagers.length; i++) {
            sceneDataManagers[i] = new SceneDataManager(UnityScenes.SCENES[i], i);
        }
    }

    @Override
    public int getPort() {
        return 2967;
    }

    @Override
    public boolean isBind() {
        return connected.get();
    }

    @Override
    public ExternalService.ProtocolType getProtocolType() {
        return ExternalService.ProtocolType.TCP;
    }

    @Override
    public String getDescription() {
        return "Server for unity gameplay";
    }

    public byte addCharacterToScene(int sceneID, String characterName) {
        return sceneDataManagers[sceneID].addCharacter(characterName);
    }

    public void removeCharacterFromScene(int sceneID, String characterName) {
        sceneDataManagers[sceneID].removeCharacter(characterName);
    }

    public synchronized void start() {
        int portIncrementer = getPort();
        int sceneIDIncrementer = 0;
        for (final String sceneName_ : UnityScenes.SCENES) {
            final int port_ = portIncrementer;
            portIncrementer++;
            
            final int sceneID_ = sceneIDIncrementer;
            sceneIDIncrementer++;
            
            sceneExecutors.execute(new Runnable() {
                @Override
                public void run() {
                    int port = port_;
                    int sceneID = sceneID_;
                    String sceneName = sceneName_;
                    
                    DatagramSocket serverSocket = null;
                    byte[] receiveData = new byte[512];
                    byte[] sendData = new byte[512];
                    try {
                        serverSocket = new DatagramSocket(port);
                        serverSocket.setSoTimeout(50);
                        connected.set(true);

                        while (running.get()) {
                            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                            try {
                                serverSocket.receive(receivePacket);
                                sendData = sceneDataManagers[sceneID].decodePacketAndComputeResponse(receivePacket.getData());
                                receivedPackets.incrementAndGet();
                                if(sendData != null) {
                                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
                                    serverSocket.send(sendPacket);
                                    sentPackets.incrementAndGet();
                                }
                            } catch(SocketTimeoutException ex) {
                                //don't do anything on timeout
                            } catch (IOException ex) {
                                Logger.getLogger(UnityServer.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } catch (SocketException ex) {
                        Logger.getLogger(UnityServer.class.getName()).log(Level.SEVERE, null, ex);
                    } finally {
                        if (serverSocket != null) {
                            serverSocket.close();
                        }
                    }
                }
            });
        }

        App.getApp().getServiceManager().register(UnityServer.class, this);
        eBus.register(this);
    }

    public synchronized void stop() {
        App.getApp().getServiceManager().unregister(UnityServer.class);
        running.set(false);
        try {
            sceneExecutors.awaitTermination(100, TimeUnit.MILLISECONDS);
            sceneExecutors.shutdownNow();
        } catch (InterruptedException ex) {
        }
        eBus.unregister(this);
    }
}
