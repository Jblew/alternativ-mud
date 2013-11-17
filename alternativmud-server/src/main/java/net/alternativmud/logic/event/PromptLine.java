/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.event;

import net.alternativmud.lib.event.NetworkEvent;

/**
 *
 * @author jblew
 */
public class PromptLine implements NetworkEvent.TextEvent {
    private final String text;
    public PromptLine(String text) {
        this.text = text;
    }
    
    @Override
    public String getText() {
        return text;
    }
}
