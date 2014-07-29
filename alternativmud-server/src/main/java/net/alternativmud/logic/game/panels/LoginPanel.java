/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.game.panels;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.App;
import net.alternativmud.logic.User;
import net.alternativmud.logic.event.ReceivedTextFromUser;
import net.alternativmud.logic.event.SendTextToUser;

/**
 *
 * @author jblew
 */
class LoginPanel {

    private final EventBus ebus;
    private State state = State.LOGIN_PROMPT;
    private String login = "";

    public LoginPanel(EventBus ebus) {
        this.ebus = ebus;

        ebus.post(new SendTextToUser("Hi! You have to log in. "
                + "If you want to create new account, type {X'new'{x, if you simply "
                + "want to log in, then type your name.\r\n"
                + "Type your name: "));
    }

    @Subscribe
    public void messageReceived(ReceivedTextFromUser evt) {
        if (state == State.LOGIN_PROMPT) {
            login = evt.getText();
            if (!login.isEmpty()) {
                if (login.equals("new") || login.equals("signup") || login.equals("sign up")) {
                    ebus.unregister(this);
                    ebus.register(new SignUpPanel(ebus));
                } else {
                    try {
                        if (App.getApp().getEntitiesManager().getUsersDao().queryForId(login) == null) {
                            ebus.post(new SendTextToUser("Sorry. There is no such user.\nType your name: "));
                        } else {
                            ebus.post(new SendTextToUser("Hi, " + login + "! Type your password: "));
                            state = State.PASSWORD_PROMPT;
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(LoginPanel.class.getName()).log(Level.SEVERE, "Could not check if user login is correct", ex);
                        ebus.post(new SendTextToUser("Sorry, there was an error. We are unable to check your credintials. Please try again later or contact system administrator."));
                    }
                }
            }
        } else if (state == State.PASSWORD_PROMPT) {
            try {
                User u = App.getApp().getEntitiesManager().getUsersDao().queryForId(login);
                if(u.isPasswordCorrect(evt.getText())) {
                    ebus.unregister(this);
                    ebus.register(new GlobalMenuPanel(ebus, u));
                } else {
                    ebus.post(new SendTextToUser("Your password is incorrect. Please login once again."));
                    state = State.LOGIN_PROMPT;
                }
            } catch (SQLException ex) {
                Logger.getLogger(LoginPanel.class.getName()).log(Level.SEVERE, "Could not check user password", ex);
                ebus.post(new SendTextToUser("Sorry, there was an error. We are unable to check your credintials. Please try again later or contact system administrator."));
            }
        }
    }

    private static enum State {

        LOGIN_PROMPT, PASSWORD_PROMPT
    }
}
