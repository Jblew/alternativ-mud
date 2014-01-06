/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package net.alternativmud.system.unityserver;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.logging.Logger;
import net.alternativmud.logic.User;
import net.alternativmud.logic.world.characters.UCharacter;
import net.alternativmud.system.nebus.server.TCPEBusServer;

/**
 *
 * @author Jędrzej Lew
 */
public class Unity3DModeSubscriber {
    private final UnityServer unityServer;
    private final EventBus ebus;
    private final UCharacter character;
    private final User user;
    private final byte sceneID;
    
    public Unity3DModeSubscriber(UnityServer unityServer, EventBus ebus, User user, UCharacter character, byte sceneID) {
        this.unityServer = unityServer;
        this.ebus = ebus;
        this.user = user;
        this.character = character;
        this.sceneID = sceneID;
        
        byte characterID = unityServer.addCharacterToScene(sceneID, character.getName());
        int port = unityServer.getPort()+sceneID;
        
        ebus.post(new SceneEnterSucceeded(port, characterID));
    }
    
    @Subscribe 
    public void ebusClosed(TCPEBusServer.EBusClosed evt) {
        Logger.getLogger(getClass().getName()).info("Ebus of "+user.getLogin()+":"+character.getName()+" closed. Unregistering Unity3DModeSubscriber, removing character from UnityServer");
        unityServer.removeCharacterFromScene(sceneID, character.getName());
        ebus.unregister(this);
    }
    
    public static class SceneEnterSucceeded {
        private int port;
        private byte characterID;

        public SceneEnterSucceeded() {}
        
        public SceneEnterSucceeded(int port, byte characterID) {
            this.port = port;
            this.characterID = characterID;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public byte getCharacterID() {
            return characterID;
        }

        public void setCharacterID(byte characterID) {
            this.characterID = characterID;
        }
    }
}
