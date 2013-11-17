/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.nebus.server;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import net.alternativmud.App;
import net.alternativmud.logic.User;
import net.alternativmud.logic.game.Gameplay;
import net.alternativmud.logic.world.characters.UCharacter;

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
}
