/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.bootstrap;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.App;
import net.alternativmud.lib.NamingThreadFactory;
import net.alternativmud.system.lifecycle.RunnableTask;
import net.alternativmud.system.remoteadmin.RemoteAdminServer;

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
                        } else {
                            System.out.println("Unrecognized command: '" + cmd + "'");
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