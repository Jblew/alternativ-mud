/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.gclient.actions;

import com.google.common.eventbus.Subscribe;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import net.alternativmud.gclient.forms.FormFrame;
import net.alternativmud.gclient.forms.LoginForm;
import net.alternativmud.gclient.forms.SelectCharacterForm;
import net.alternativmud.system.nebus.server.NetworkBusClient;
import net.alternativmud.system.nebus.server.StandardBusSubscriber;

/**
 *
 * @author jblew
 */
class Login implements Runnable {
    private final ExecutorService executor;
    private final FormFrame formFrame;
    private final NetworkBusClient clientBus;
    public Login(ExecutorService executor, FormFrame formFrame, NetworkBusClient clientBus) {
        this.executor = executor;
        this.formFrame = formFrame;
        this.clientBus = clientBus;

        System.out.println("Login.construct");
    }

    @Override
    public void run() {
        final LoginForm loginForm = new LoginForm();
        formFrame.setForm(loginForm);

        loginForm.getLoginButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        clientBus.register(this);
                        clientBus.post(new StandardBusSubscriber.PerformLogin(loginForm.getLoginField().getText(),
                                loginForm.getPasswordField().getText()));
                        loginForm.getMessageLabel().setForeground(Color.BLUE);
                        loginForm.getMessageLabel().setText("Logging in...");
                    }

                    @Subscribe
                    public void loginFailed(StandardBusSubscriber.LoginFailed evt) {
                        loginForm.getMessageLabel().setForeground(Color.RED);
                        loginForm.getMessageLabel().setText("Login failed. Bad login or password.");
                    }

                    @Subscribe
                    public void loginSucceeded(StandardBusSubscriber.LoginSucceeded evt) {
                        loginForm.getMessageLabel().setForeground(Color.GREEN);
                        loginForm.getMessageLabel().setText("Login succeeded.");
                        clientBus.unregister(this);
                        
                        executor.execute(new SelectCharacter(executor, formFrame, clientBus, evt.getUser()));
                    }

                });
            }

        });
        formFrame.setForm(loginForm);
    }

}
