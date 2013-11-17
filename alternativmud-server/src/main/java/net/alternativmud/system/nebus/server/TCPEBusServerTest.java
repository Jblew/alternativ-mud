/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.nebus.server;

import com.google.common.eventbus.Subscribe;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.App;
import net.alternativmud.system.tester.RuntimeTest;
import net.alternativmud.system.tester.TestException;

/**
 *
 * @author maciek
 */
public class TCPEBusServerTest implements RuntimeTest {

    @Override
    public void execute() throws TestException {
        if (App.getApp().getServiceManager().isRegistered(TCPEBusServer.class)) {
            TCPEBusServer srv = (TCPEBusServer) App.getApp().getServiceManager().getService(TCPEBusServer.class);
            try {
                NetworkBusClient clt = new NetworkBusClient(new InetSocketAddress("localhost", srv.getPort()));


                final AtomicBoolean gotEventFromServer = new AtomicBoolean(false);
                //final AtomicInteger clientCounter = new AtomicInteger();
                Object clientSubscriber = new Object() {

                    @Subscribe
                    public void gotEventFromServer(Object o) {
                        System.out.println("Client got message of type " + o.getClass().getName() + ": " + o.toString());
                        if (o instanceof StandardBusSubscriber.Status) {
                            gotEventFromServer.set(true);
                        }
                    }
                };

                clt.register(clientSubscriber);
                clt.post(new StandardBusSubscriber.GetStatus());
                for (int i = 0; i < 15; i++) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException ex) {
                    }
                    if (gotEventFromServer.get()) {
                        break;
                    }
                }
                clt.close();
                if (!gotEventFromServer.get()) {
                    throw new TestException(this, "Client has not got event from server.");
                }
            } catch (IOException ex) {
                throw new TestException(this, ex);
            }

        }
    }

    @Override
    public String getName() {
        return "TCPEBusServerTest";
    }
}
