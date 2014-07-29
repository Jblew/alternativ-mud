/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.nebus.server;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.App;
import net.alternativmud.lib.containers.TwoTuple;
import net.alternativmud.logic.User;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * StandardBusSubscriber nasłuchuje eventów ze zdalnej szyny uruchomionej na
 * serwerze TCPEBusServer.
 *
 * @author jblew
 */
public class StandardBusSubscriber {
    private final EventBus ebus;
    public StandardBusSubscriber(EventBus ebus) {
        this.ebus = ebus;
    }

    @Subscribe
    public void login(PerformLogin evt) {
        try {
            if (App.getApp().getEntitiesManager().getUsersDao() == null) {
                Logger.getLogger(StandardBusSubscriber.class.getName()).log(Level.SEVERE, "UsersDao is null, could not perform login");
                ebus.post(new LoginFailed());
            } else {
                User user = App.getApp().getEntitiesManager().getUsersDao().queryForId(evt.getLogin());
                if (user != null && user.isPasswordCorrect(evt.getPassword())) {
                    ebus.post(new LoginSucceeded(user));
                    ebus.register(new AuthenticatedBusSubscriber(ebus, user));
                } else {
                    ebus.post(new LoginFailed());
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(StandardBusSubscriber.class.getName()).log(Level.SEVERE, "Could not perform login", ex);
            ebus.post(new LoginFailed());
        }
    }

    @Subscribe
    public void getStatus(GetStatus evt) {
        Status s = new Status();
        s.setTitle("AltertnativMUD");
        s.setSubtitle("Modern MUD Software");
        ebus.post(s);
    }

    public static class PerformLogin {
        private String login = "";
        private String password = "";
        public PerformLogin() {
        }

        public PerformLogin(String login, String password) {
            setLogin(login);
            setPassword(password);
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }

    public static final class LoginSucceeded {
        private final User user;

        @JsonCreator
        public LoginSucceeded(@JsonProperty("user") User user) {
            this.user = user;
        }

        @JsonProperty("user")
        public User getUser() {
            return user;
        }
    }

    public static final class LoginFailed {
    }

    public static final class GetStatus {}

    public static final class Status {
        private String title = "";
        private String subtitle = "";


        public String getSubtitle() {
            return subtitle;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

    }
}
