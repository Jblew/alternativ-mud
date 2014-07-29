/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.event;

import net.alternativmud.lib.event.NetworkEvent.TextEvent;

/**
 *
 * @author jblew
 */
public class SendTextToUser implements TextEvent {
    private String text = "text";
    public SendTextToUser(String text) {
        this.text = text;
    }

    public SendTextToUser() {
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

}
