/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.bootstrap;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.App;
import net.alternativmud.game3d.UnityScenes;
import net.alternativmud.lib.NamingThreadFactory;
import net.alternativmud.logic.world.characters.UCharacter;
import net.alternativmud.system.lifecycle.RunnableTask;
import net.alternativmud.system.remoteadmin.RemoteAdminServer;
import net.alternativmud.system.unityserver.Unity3DModeSubscriber;

/**
 *
 * @author jblew
 */
public class StartConsoleReader implements RunnableTask {

    @Override
    public String getDescription() {
        return "Start ConsoleReader";
    }

    @Override
    public boolean shouldBeExecuted() {
        return true;
    }

    @Override
    public void execute() throws Exception {
        final ExecutorService executor = Executors.newCachedThreadPool(
                new NamingThreadFactory("console-reader"));

        final PushbackReader pushback = new PushbackReader(new InputStreamReader(System.in));
        final BufferedReader reader = new BufferedReader(pushback);

        executor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    while (true) {

                        String cmd = reader.readLine();
                        if (cmd.equals("stop")) {
                            App.getApp().getLifecycle().shutdown();
                        } else if (cmd.startsWith("setvar")) {
                            String[] parts = cmd.split(" ");
                            if (parts.length == 3) {
                                App.getApp().getVariablesManager().setValue(parts[1], parts[2]);
                                for (byte dstSceneID : UnityScenes.getScenesUsingVariable(parts[1])) {
                                    if (App.getApp().getUnityBusCharacterPool().charactersInScenes.containsKey(dstSceneID)) {
                                        Map<Byte, UCharacter> charactersInScene = App.getApp().getUnityBusCharacterPool().charactersInScenes.get(dstSceneID);
                                        for (byte sceneCharacterID : charactersInScene.keySet()) {
                                            if (App.getApp().getUnityBusCharacterPool().characterBuses.containsKey(charactersInScene.get(sceneCharacterID))) {
                                                App.getApp().getUnityBusCharacterPool().characterBuses.get(charactersInScene.get(sceneCharacterID)).post(new Unity3DModeSubscriber.VariableChanged(parts[1], parts[2]));
                                            }
                                        }
                                    }
                                }
                            } else {
                                System.out.println("[>] 'setvar' usage: setvar [key] [value]");
                            }
                        } else if (cmd.startsWith("_setvar")) {
                            try {
                                TimeUnit.SECONDS.sleep(2);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(StartConsoleReader.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            String[] parts = cmd.split(" ");
                            if (parts.length == 3) {
                                App.getApp().getVariablesManager().setValue(parts[1], parts[2]);
                                for (byte dstSceneID : UnityScenes.getScenesUsingVariable(parts[1])) {
                                    if (App.getApp().getUnityBusCharacterPool().charactersInScenes.containsKey(dstSceneID)) {
                                        Map<Byte, UCharacter> charactersInScene = App.getApp().getUnityBusCharacterPool().charactersInScenes.get(dstSceneID);
                                        for (byte sceneCharacterID : charactersInScene.keySet()) {
                                            if (App.getApp().getUnityBusCharacterPool().characterBuses.containsKey(charactersInScene.get(sceneCharacterID))) {
                                                App.getApp().getUnityBusCharacterPool().characterBuses.get(charactersInScene.get(sceneCharacterID)).post(new Unity3DModeSubscriber.VariableChanged(parts[1], parts[2]));
                                            }
                                        }
                                    }
                                }
                            } else {
                                System.out.println("[>] 'setvar' usage: setvar [key] [value]");
                            }
                        } else {
                            System.out.println("[>] Unrecognized command: '" + cmd + "'");
                        }
                        try {
                            TimeUnit.MILLISECONDS.sleep(10);
                        } catch (InterruptedException ex) {
                            //do nothing on interrupted exception
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(StartConsoleReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        App.getApp().getLifecycle().registerShutdownTask(new RunnableTask() {

            @Override
            public String getDescription() {
                return "Stop ConsoleReader";
            }

            @Override
            public boolean shouldBeExecuted() {
                return true;
            }

            @Override
            public void execute() throws Exception {
                try {
                    pushback.unread("a\r\n".toCharArray());
                } catch (IOException ex) {
                    Logger.getLogger(StartConsoleReader.class.getName()).log(Level.FINE, null, ex);
                }
                reader.close();
                executor.shutdown();
                if (!executor.awaitTermination(50, TimeUnit.MILLISECONDS)) {
                    executor.shutdownNow();
                }
            }
        });
    }
}
