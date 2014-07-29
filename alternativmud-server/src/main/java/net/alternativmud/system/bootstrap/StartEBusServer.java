/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.bootstrap;

import net.alternativmud.App;
import net.alternativmud.system.nebus.server.TCPEBusServer;
import net.alternativmud.system.lifecycle.RunnableTask;
import net.alternativmud.system.nebus.server.TCPEBusServerTest;
import net.alternativmud.system.nebus.server.WSEBusServer;
import net.alternativmud.system.tester.Tester;

/**
 *
 * @author maciek
 */
public class StartEBusServer implements RunnableTask {

    @Override
    public String getDescription() {
        return "Start EBus servers";
    }

    @Override
    public boolean shouldBeExecuted() {
        return !(App.getApp().getServiceManager().isRegistered(TCPEBusServer.class)
                && App.getApp().getServiceManager().isRegistered(WSEBusServer.class)); //execute if telnet service was not registered.
    }

    @Override
    public void execute() throws Exception {
        final TCPEBusServer srv = new TCPEBusServer(App.getApp().getSystemEventBus());
        final WSEBusServer srv2 = new WSEBusServer(App.getApp().getSystemEventBus());
        final TCPEBusServerTest test = new TCPEBusServerTest();
        srv.start();
        srv2.start();
        App.getApp().getServiceManager().register(TCPEBusServer.class, srv);
        App.getApp().getServiceManager().register(WSEBusServer.class, srv);
        if (App.getApp().getServiceManager().isRegistered(Tester.class)) {
            ((Tester) App.getApp().getServiceManager().getService(Tester.class)).addTest(test);
        }

        App.getApp().getLifecycle().registerShutdownTask(new RunnableTask() {

            @Override
            public String getDescription() {
                return "Stop EBus Servers";
            }

            @Override
            public boolean shouldBeExecuted() {
                return App.getApp().getServiceManager().isRegistered(TCPEBusServer.class)
                        || App.getApp().getServiceManager().isRegistered(WSEBusServer.class); //jesli serwer jest zarejestrowany
            }

            @Override
            public void execute() throws Exception {
                App.getApp().getServiceManager().unregister(TCPEBusServer.class);
                App.getApp().getServiceManager().unregister(WSEBusServer.class);
                srv.stop();
                srv2.stop();
                if (App.getApp().getServiceManager().isRegistered(Tester.class)) {
                    ((Tester) App.getApp().getServiceManager().getService(Tester.class)).removeTest(test);
                }
            }
        });
    }
}