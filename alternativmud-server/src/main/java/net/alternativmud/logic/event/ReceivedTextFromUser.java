/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.event;

import net.alternativmud.lib.event.NetworkEvent.TextEvent;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author jblew
 */
public class ReceivedTextFromUser implements TextEvent {
    private final String text;
    
    @JsonCreator
    public ReceivedTextFromUser(@JsonProperty("text") String text) {
        this.text = text;
    }
    
    @Override
    public String getText() {
        return text;
    }
}
