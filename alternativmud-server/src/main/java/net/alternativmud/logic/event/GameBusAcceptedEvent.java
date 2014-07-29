/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.event;

import com.google.common.eventbus.EventBus;

/**
 *
 * @author jblew
 */
public class GameBusAcceptedEvent {
    private final EventBus bus;
    public GameBusAcceptedEvent(EventBus bus) {
        this.bus = bus;
    }

    public EventBus getBus() {
        return bus;
    }
}
