/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.telnetserver;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.lib.util.StringWriter;
import net.alternativmud.logic.event.CloseBus;
import net.alternativmud.logic.event.DisplayPrompt;
import net.alternativmud.logic.event.PromptLine;
import net.alternativmud.logic.event.SendTextToUser;
import org.jboss.netty.channel.Channel;

/**
 *
 * @author jblew
 */
public class TelnetConnectionBus extends EventBus implements Closeable, StringWriter {
    private final Channel chan;
    TelnetConnectionBus(final Channel chan) {
        this.chan = chan;
        this.register(new Object() {
            //AtomicBoolean requestedPromptDisplay = new AtomicBoolean(false);
            //AtomicBoolean promptAtTheTop = new AtomicBoolean(false);
            
            @Subscribe
            public void sendTextToUser(SendTextToUser evt) {
                try {
                    print(Color.colorify(evt.getText()));
                    //promptAtTheTop.set(false);
                } catch (IOException ex) {
                    Logger.getLogger(TelnetConnectionBus.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                //if(requestedPromptDisplay.get()) {
                //    requestedPromptDisplay.set(false);
                //}
                //else {
                //    requestedPromptDisplay.set(true);
                    post(new DisplayPrompt());
                //}
            }
            
            @Subscribe
            public void promptLine(PromptLine evt) {
                try {
                    print(Color.colorify(evt.getText()));
                    //promptAtTheTop.set(true);
                } catch (IOException ex) {
                    Logger.getLogger(TelnetConnectionBus.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            @Subscribe
            public void closeBus(CloseBus evt) {
                try {
                    print("Bye! Closing socket.\r\n");
                    chan.close();
                } catch (IOException ex) {
                    Logger.getLogger(TelnetConnectionBus.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    @Override
    public void close() throws IOException {
        chan.close();
    }

    @Override
    public void print(String s) throws IOException {
        chan.write(s);
    }

    @Override
    public void println(String s) throws IOException {
        print(s+"\r\n");
    }
}
