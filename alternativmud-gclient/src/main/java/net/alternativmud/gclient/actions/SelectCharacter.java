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
import javax.swing.DefaultListModel;
import javax.swing.JList;
import net.alternativmud.gclient.forms.FormFrame;
import net.alternativmud.gclient.forms.LoginForm;
import net.alternativmud.gclient.forms.SelectCharacterForm;
import net.alternativmud.logic.User;
import net.alternativmud.logic.time.TimeMachine;
import net.alternativmud.logic.world.characters.UCharacter;
import net.alternativmud.system.nebus.server.AuthenticatedBusSubscriber;
import net.alternativmud.system.nebus.server.NetworkBusClient;
import net.alternativmud.system.nebus.server.StandardBusSubscriber;

/**
 *
 * @author jblew
 */
class SelectCharacter implements Runnable {
    private final ExecutorService executor;
    private final FormFrame formFrame;
    private final NetworkBusClient clientBus;
    private final User user;
    public SelectCharacter(ExecutorService executor, FormFrame formFrame, NetworkBusClient clientBus, User user) {
        this.executor = executor;
        this.formFrame = formFrame;
        this.user = user;
        this.clientBus = clientBus;
    }

    @Override
    public void run() {
        final SelectCharacterForm selectCharacterForm = new SelectCharacterForm();
        formFrame.setForm(selectCharacterForm);

        selectCharacterForm.getMessageLabel().setForeground(Color.BLUE);
        selectCharacterForm.getMessageLabel().setText("Downloading character list...");
        
        clientBus.register(new Object() {
            @Subscribe
            public void gotCharactersList(AuthenticatedBusSubscriber.Characters evt) {
                JList list = selectCharacterForm.getCharacterList();
                DefaultListModel listModel = new DefaultListModel();
                for (UCharacter c : evt.getCharacters()) {
                    listModel.addElement(c.getName());
                }
                list.setModel(listModel);
                list.setSelectedIndex(0);
                clientBus.unregister(this);
                selectCharacterForm.getMessageLabel().setText("");
            }

        });
        
        clientBus.post(new AuthenticatedBusSubscriber.GetCharacters());

        selectCharacterForm.getPlayButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                final Object selected = selectCharacterForm.getCharacterList().getSelectedValue();
                if (selected == null) {
                    selectCharacterForm.getMessageLabel().setForeground(Color.RED);
                    selectCharacterForm.getMessageLabel().setText("Please select a character.");
                } else {
                    executor.execute(new Runnable() {
                        private TimeMachine timeMachine = null;
                        
                        @Override
                        public void run() {
                            clientBus.register(this);

                            selectCharacterForm.getMessageLabel().setForeground(Color.BLUE);
                            selectCharacterForm.getMessageLabel().setText("Requesting time machine...");
                            clientBus.post(new AuthenticatedBusSubscriber.GetTimeMachine());
                        }
                        
                        @Subscribe
                        public void timeMachine(TimeMachine timeMachine) {
                            this.timeMachine = timeMachine;
                            
                            selectCharacterForm.getMessageLabel().setForeground(Color.BLUE);
                            selectCharacterForm.getMessageLabel().setText("Requesting gameplay start.");
                            clientBus.post(new AuthenticatedBusSubscriber.StartGameplay((String) selected));
                        }

                        @Subscribe
                        public void gameplayStarted(AuthenticatedBusSubscriber.GameplayStarted evt) {
                            selectCharacterForm.getMessageLabel().setForeground(Color.GREEN);
                            selectCharacterForm.getMessageLabel().setText("Gameplay started.");
                            
                            formFrame.setVisible(false);
                            executor.execute(new Play(executor, formFrame, clientBus, user, timeMachine, (String) selected));
                        }

                        @Subscribe
                        public void gameplayStartFailed(AuthenticatedBusSubscriber.GameplayStartFailed evt) {
                            selectCharacterForm.getMessageLabel().setForeground(Color.RED);
                            selectCharacterForm.getMessageLabel().setText("Gameplay start failed: " + evt.getMessage() + ".");
                        }

                    });
                }
            }

        });
        formFrame.setForm(selectCharacterForm);
    }

}
