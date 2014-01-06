/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.nebus.server;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import net.alternativmud.App;
import net.alternativmud.framework.Service;
import net.alternativmud.game3d.UnityScenes;
import net.alternativmud.logic.User;
import net.alternativmud.logic.game.Gameplay;
import net.alternativmud.logic.world.characters.UCharacter;
import net.alternativmud.system.nebus.server.TCPEBusServer.EBusClosed;
import net.alternativmud.system.unityserver.Unity3DModeSubscriber;
import net.alternativmud.system.unityserver.UnityServer;

/**
 *
 * @author jblew
 */
public class AuthenticatedBusSubscriber {
    private final EventBus ebus;
    private final User user;
    public AuthenticatedBusSubscriber(EventBus ebus, User user) {
        this.ebus = ebus;
        this.user = user;
    }

    @Subscribe
    public void getCharacters(GetCharacters evt) {
        ebus.post(new Characters(
                App.getApp().getWorld().getCharactersManager().getCharactersForUser(user.getLogin())));
    }

    @Subscribe
    public void startGameplay(StartGameplay evt) {
        ebus.register(
                new Gameplay(ebus, user,
                App.getApp().getWorld().getCharactersManager().getCharacter(evt.getCharacterName())));
        ebus.post(new GameplayStarted());
    }

    @Subscribe
    public void getTimeMachine(GetTimeMachine evt) {
        ebus.post(App.getApp().getWorld().getTimeMachine());
    }
    
    @Subscribe
    public void enterUnity3DMode(EnterUnity3DMode evt) {
        try {
            byte sceneID = UnityScenes.getSceneID(evt.getSceneName());
            Service service =  App.getApp().getServiceManager().getService(UnityServer.class);
            if(service != null && service instanceof UnityServer) {
                UnityServer srv = (UnityServer) service;
                UCharacter character = App.getApp().getWorld().getCharactersManager().getCharacter(evt.getCharacterName());
                ebus.register(new Unity3DModeSubscriber(srv, ebus, user, character, sceneID));
            }
            else {
                ebus.post(new Unity3DModeEnterFailed("Unity server is not registered in ServiceManager"));
            }
        } catch(NoSuchElementException e) {
            ebus.post(new Unity3DModeEnterFailed("No such scene on server"));
        }
    }
    
    @Subscribe 
    public void logout(Logout evt) {
        Logger.getLogger(getClass().getName()).info("User "+user.getLogin()+" logged out. Unregistering AuthenticatedBusSubscriber");
        ebus.unregister(this);
    }
    
    @Subscribe 
    public void ebusClosed(EBusClosed evt) {
        Logger.getLogger(getClass().getName()).info("Ebus of "+user.getLogin()+" closed. Unregistering AuthenticatedBusSubscriber");
        ebus.unregister(this);
    }

    public static class GetCharacters {
    }

    public static class Characters {
        private List<UCharacter> characters;
        public Characters(List<UCharacter> characters) {
            this.characters = characters;
        }

        public Characters() {
        }

        public List<UCharacter> getCharacters() {
            return characters;
        }

        public void setCharacters(List<UCharacter> characters) {
            this.characters = characters;
        }

    }

    public static class StartGameplay {
        private String characterName;
        public StartGameplay(String characterName) {
            this.characterName = characterName;
        }

        public StartGameplay() {
        }

        public String getCharacterName() {
            return characterName;
        }

        public void setCharacterName(String characterName) {
            this.characterName = characterName;
        }

    }

    public static class Logout {
        
    }
    
    public static class GameplayStarted {
    }

    public static class GameplayStartFailed {
        private String message;
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
    
    public static class GetTimeMachine {}
    
    public static class EnterUnity3DMode {
        private String sceneName;
        private String characterName;

        public EnterUnity3DMode() {}
        
        public EnterUnity3DMode(String sceneName, String characterName) {
            this.sceneName = sceneName;
            this.characterName = characterName;
        }
        
        public void setSceneName(String sceneName) {
            this.sceneName = sceneName;
        }

        public String getSceneName() {
            return sceneName;
        }

        public String getCharacterName() {
            return characterName;
        }

        public void setCharacterName(String characterName) {
            this.characterName = characterName;
        }
    }
    
    public static class Unity3DModeEnterFailed {
        private String message;
        
        public Unity3DModeEnterFailed() {}

        public Unity3DModeEnterFailed(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
