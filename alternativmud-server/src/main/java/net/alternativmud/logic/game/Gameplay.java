/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.App;
import net.alternativmud.lib.NamingThreadFactory;
import net.alternativmud.logic.time.TimeValue;
import net.alternativmud.lib.containers.TwoTuple;
import net.alternativmud.logic.User;
import net.alternativmud.logic.event.*;
import net.alternativmud.logic.game.commands.*;
import net.alternativmud.logic.game.panels.GlobalMenuPanel;
import net.alternativmud.logic.geo.Coordinates3l;
import net.alternativmud.logic.world.area.Location;
import net.alternativmud.logic.world.characters.UCharacter;
import net.alternativmud.system.nebus.server.AuthenticatedBusSubscriber;
import net.alternativmud.system.nebus.server.TCPEBusServer.EBusClosed;

/**
 *
 * @author jblew
 */
public class Gameplay {
    private final EventBus ebus;
    private final User user;
    private final UCharacter character;
    private final List<TwoTuple<String, Command>> commandsList = new LinkedList<>();
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2, new NamingThreadFactory("gameplay-scheduled"));
    public Gameplay(EventBus ebus, User user, UCharacter character) {
        this.ebus = ebus;
        this.user = user;
        this.character = character;

        commandsList.add(new TwoTuple<String, Command>("look", new Look()));
        commandsList.add(new TwoTuple<String, Command>("south", new South()));
        commandsList.add(new TwoTuple<String, Command>("north", new North()));
        commandsList.add(new TwoTuple<String, Command>("west", new West()));
        commandsList.add(new TwoTuple<String, Command>("east", new East()));
        commandsList.add(new TwoTuple<String, Command>("up", new Up()));
        commandsList.add(new TwoTuple<String, Command>("down", new Down()));
        commandsList.add(new TwoTuple<String, Command>("map", new Map()));
        commandsList.add(new TwoTuple<String, Command>("help", new Help()));
        commandsList.add(new TwoTuple<String, Command>("flee", new Flee()));
        commandsList.add(new TwoTuple<String, Command>("kill", new Kill()));
        commandsList.add(new TwoTuple<String, Command>("say", new Say()));
        commandsList.add(new TwoTuple<String, Command>("time", new Time()));
        commandsList.add(new TwoTuple<String, Command>("quit", new Quit()));
        commandsList.add(new TwoTuple<String, Command>("who", new Who()));

        App.getApp().getSystemEventBus().post(new GameplayStarted(this));

        scheduleTask(new TimeValue(App.getApp().getWorld().getTimeMachine().getConfig().getPulseDurationMs(), TimeUnit.MILLISECONDS),
                new Runnable() {
                    @Override
                    public void run() {
                        if (getCharacter().isInFight()) {
                            getEBus().post(new DisplayPrompt());
                        }
                    }

                });

        ebus.post(new LocationChanged(
                App.getApp().getWorld().getLocationsManager().getLocation(
                character.getLocationCoordinates())));
    }
    
    @Subscribe
    public void gameplayClientReady(GameplayClientReady evt) {
        ebus.post(new LocationChanged(
                App.getApp().getWorld().getLocationsManager().getLocation(
                character.getLocationCoordinates())));
    }

    @Subscribe
    public void messageReceived(ReceivedTextFromUser evt) {
        String userInput = evt.getText();

        String[] parts = userInput.split(" ");
        if (parts[0].isEmpty()) {
            println("");
            return;
        }

        System.out.println(user.getLogin() + "." + character.getName() + " > " + userInput);
        boolean executed = false;
        String errorMsg = "";
        for (TwoTuple<String, Command> cmd : commandsList) {
            if (cmd.a.startsWith(parts[0])) {
                try {
                    println(cmd.b.execute(this, parts) + "\r\n");
                    executed = true;
                    break;
                } catch (ExecutionRejectedException ex) {
                    //Wyswietlamy tylko pierwszy error
                    if (errorMsg.isEmpty()) {
                        errorMsg = ex.getMessage();
                    }
                } catch (Exception e) {
                    Logger.getLogger(Gameplay.class.getName()).log(Level.WARNING,
                            "Exception in command " + cmd.a, e);
                    if (getUser().getAdmin()) {
                        String msg = "{RException in command '" + cmd.a + "':{x\r\n"
                                + "  Message: " + e.getMessage() + "\r\n"
                                + "  Stack trace: \r\n";
                        for (StackTraceElement ste : e.getStackTrace()) {
                            msg += "   " + ste.toString() + "\r\n";
                        }
                        msg += "\r\n";
                    }
                }
            }
        }
        if (!executed) {
            if (!errorMsg.isEmpty()) {
                println(errorMsg + "\r\n");
            } else {
                println("What?\r\n");
            }
        }
    }

    @Subscribe
    public void displayPrompt(DisplayPrompt evt) {
        Location l = App.getApp().getWorld().getLocationsManager().getLocation(
                getCharacter().getLocationCoordinates());
        if (getCharacter().isInFight()) {
            ebus.post(new PromptLine("{G<{X" + getCharacter().getName() + "{x {X" + getCharacter().getHealthPoints() + "{xhp>"
                    + " [" + l.getCoordinates().getRadius() + ","
                    + l.getCoordinates().getLongitude() + "," + l.getCoordinates().getLatitude() + "]"
                    + "{x {R" + getCharacter().getFight() + "{x"));
        } else {
            ebus.post(new PromptLine("{G<{X" + getCharacter().getName() + "{x {X" + getCharacter().getHealthPoints() + "{xhp>"
                    + " [" + l.getCoordinates().getRadius() + ","
                    + l.getCoordinates().getLongitude() + "," + l.getCoordinates().getLatitude() + "]{x "));
        }
    }

    @Subscribe
    public void ebusClosed(EBusClosed evt) {
        App.getApp().getSystemEventBus().post(new GameplayFinished(this));
        ebus.unregister(this);
    }

    public UCharacter getCharacter() {
        return character;
    }

    public EventBus getEBus() {
        return ebus;
    }

    public User getUser() {
        return user;
    }

    public void println(String msg) {
        ebus.post(new SendTextToUser(msg + "\r\n"));
    }

    /**
     * Mark output as dirty. If output is dirty, it needs prompt line.
     */
    public void markDirty() {
        //ebus.post();
    }

    public ScheduledFuture<?> scheduleTask(TimeValue time, Runnable r) {
        return executor.scheduleAtFixedRate(r, time.value, time.value, time.unit);
    }

    public void quit() {
        App.getApp().getSystemEventBus().post(new GameplayFinished(this));
        ebus.unregister(this);
        ebus.register(new GlobalMenuPanel(ebus, user));
    }

}
