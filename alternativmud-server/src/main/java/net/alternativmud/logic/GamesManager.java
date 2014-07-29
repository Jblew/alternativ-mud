/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic;

import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.alternativmud.App;
import net.alternativmud.logic.event.GameBusAcceptedEvent;
import net.alternativmud.logic.event.GameplayFinished;
import net.alternativmud.logic.event.GameplayStarted;
import net.alternativmud.logic.game.Gameplay;
import net.alternativmud.logic.game.panels.IntroPanel;

/**
 * Ta klasa przechwytuje nadchodzące połączenia telnetu i deleguje je do
 * łańcucha paneli.
 *
 * @author jblew
 */
public class GamesManager {
    private final List<Gameplay> gameplays = Collections.synchronizedList(new ArrayList<Gameplay>());
    public GamesManager() {
    }

    public void bootstrap() {
        App.getApp().getSystemEventBus().register(this);
    }

    public void shutdown() {
        App.getApp().getSystemEventBus().unregister(this);
    }

    /**
     * Ta metoda zostanie zdalnie wykonana po połączeniu użytkownika.
     */
    @Subscribe
    public void newUserConnected(GameBusAcceptedEvent evt) {
        evt.getBus().register(new IntroPanel(evt.getBus()));
    }

    @Subscribe
    public void gameplayStarted(GameplayStarted evt) {
        gameplays.add(evt.getGameplay());
    }
    
    @Subscribe
    public void gameplayFinished(GameplayFinished evt) {
        gameplays.remove(evt.getGameplay());
    }
    
    public List<Gameplay> getGameplays() {
        return gameplays;
    }
}
