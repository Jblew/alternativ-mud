/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.ebusserver;

import net.alternativmud.system.nebus.server.TCPEBusServer;
import net.alternativmud.system.nebus.server.NetworkBusClient;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import net.alternativmud.lib.debug.BusLogger;

/**
 *
 * @author jblew
 */
public class TCPEBusServerTest extends TestCase {

    public TCPEBusServerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSendFromServerToClient() throws Exception {
        System.out.println("testSendFromServerToClient");
        EventBus globalBus = new EventBus("global");
        globalBus.register(new BusLogger());

        TCPEBusServer ebusServer = new TCPEBusServer(globalBus);
        ebusServer.start();

        assertTrue("EBusServer is not bind", ebusServer.isBind());

        NetworkBusClient clientSideBusEnd = new NetworkBusClient(new InetSocketAddress("127.0.0.1", ebusServer.getPort()));
        for (int i = 0; i < 15; i++) {
            TimeUnit.MILLISECONDS.sleep(100);
            if (!ebusServer.getBuses().isEmpty()) {
                break;
            }
        }
        assertTrue("Bus has not connected to server.", !ebusServer.getBuses().isEmpty());

        EventBus serverSideBusEnd = ebusServer.getBuses().entrySet().iterator().next().getValue();

        final AtomicBoolean gotEventFromServer = new AtomicBoolean(false);
        //final AtomicInteger clientCounter = new AtomicInteger();
        Object clientSubscriber = new Object() {

            @Subscribe
            public void gotEventFromServer(Object o) {
                System.out.println("Client got message of type " + o.getClass().getName() + ": " + o.toString());
                if (o instanceof TestEventFromServer) {
                    gotEventFromServer.set(true);
                } else {
                    fail("Client got from server event of unsupported type " + o.getClass().getName() + ": " + o.toString());
                }
            }
        };

        clientSideBusEnd.register(clientSubscriber);
        serverSideBusEnd.post(new TestEventFromServer("msg from server"));
        for (int i = 0; i < 15; i++) {
            TimeUnit.MILLISECONDS.sleep(100);
            if (gotEventFromServer.get()) {
                break;
            }
        }
        clientSideBusEnd.unregister(clientSubscriber);
        //System.out.println("OK");
        assertTrue("Client has not got event from server.", gotEventFromServer.get());

        //System.out.println("Closing clientSideBusEnd...");
        clientSideBusEnd.close();
        //System.out.println("Stopping ebusServer...");
        ebusServer.stop();

        //System.out.println("END");
    }

    public void testSendFromClientToServer() throws Exception {
        System.out.println("testSendFromClientToServer");
        EventBus globalBus = new EventBus("global");
        globalBus.register(new BusLogger());


        TCPEBusServer ebusServer = new TCPEBusServer(globalBus);
        ebusServer.start();

        assertTrue("EBusServer is not bind", ebusServer.isBind());

        NetworkBusClient clientSideBusEnd = new NetworkBusClient(new InetSocketAddress("127.0.0.1", ebusServer.getPort()));
        for (int i = 0; i < 15; i++) {
            TimeUnit.MILLISECONDS.sleep(100);
            if (!ebusServer.getBuses().isEmpty()) {
                break;
            }
        }
        assertTrue("Bus has not connected to server.", !ebusServer.getBuses().isEmpty());

        EventBus serverSideBusEnd = ebusServer.getBuses().entrySet().iterator().next().getValue();

        final AtomicBoolean gotEventFromClient = new AtomicBoolean(false);
        //final AtomicInteger clientCounter = new AtomicInteger();
        Object serverSubscriber = new Object() {

            @Subscribe
            public void gotEventFromClient(Object o) {
                System.out.println("Server got message of type " + o.getClass().getName() + ": " + o.toString());
                if (o instanceof TestEventFromServer) {
                    gotEventFromClient.set(true);
                } else {
                    fail("Server got from server event of unsupported type " + o.getClass().getName() + ": " + o.toString());
                }
            }
        };

        serverSideBusEnd.register(serverSubscriber);
        clientSideBusEnd.post(new TestEventFromServer("msg from client"));
        for (int i = 0; i < 15; i++) {
            TimeUnit.MILLISECONDS.sleep(100);
            if (gotEventFromClient.get()) {
                break;
            }
        }
        serverSideBusEnd.unregister(serverSubscriber);
        
        //System.out.println("OK");
        assertTrue("Server has not got event from client.", gotEventFromClient.get());

        //System.out.println("Closing clientSideBusEnd...");
        clientSideBusEnd.close();
        //System.out.println("Stopping ebusServer...");
        ebusServer.stop();

        //System.out.println("END");
    }

    public void testCloseServer() throws IOException, TimeoutException, InterruptedException, ExecutionException {
        EventBus globalBus = new EventBus("global");
        globalBus.register(new BusLogger());


        final TCPEBusServer ebusServer = new TCPEBusServer(globalBus);
        ebusServer.start();
        NetworkBusClient clientSideBusEnd = new NetworkBusClient(new InetSocketAddress("127.0.0.1", ebusServer.getPort()));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> closeFuture = executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    ebusServer.stop();
                } catch (IOException ex) {
                    Logger.getLogger(TCPEBusServerTest.class.getName()).log(Level.SEVERE, null, ex);
                    fail("IOException: "+ex.getMessage());
                }
            }
        });
        closeFuture.get(250, TimeUnit.MILLISECONDS);
        executor.shutdownNow();
        assertTrue("Server not stopped.", closeFuture.isDone());
    }
    
    public void testCloseClient() throws IOException, TimeoutException, InterruptedException, ExecutionException {
        EventBus globalBus = new EventBus("global");
        globalBus.register(new BusLogger());


        final TCPEBusServer ebusServer = new TCPEBusServer(globalBus);
        ebusServer.start();
        final NetworkBusClient clientSideBusEnd = new NetworkBusClient(new InetSocketAddress("127.0.0.1", ebusServer.getPort()));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        
        Future<?> closeFuture = executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    clientSideBusEnd.close();
                } catch (IOException ex) {
                    Logger.getLogger(TCPEBusServerTest.class.getName()).log(Level.SEVERE, null, ex);
                    fail("IOException: "+ex.getMessage());
                }
            }
        });
        closeFuture.get(250, TimeUnit.MILLISECONDS);
        executor.shutdownNow();
        assertTrue("Client not stopped.", closeFuture.isDone());
        
        executor = Executors.newSingleThreadExecutor();
        closeFuture = executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    ebusServer.stop();
                } catch (IOException ex) {
                    Logger.getLogger(TCPEBusServerTest.class.getName()).log(Level.SEVERE, null, ex);
                    fail("IOException: "+ex.getMessage());
                }
            }
        });
        closeFuture.get(250, TimeUnit.MILLISECONDS);
        executor.shutdownNow();
        assertTrue("Server not stopped.", closeFuture.isDone());
    }

    public static class TestEventFromServer {

        private String name;

        public TestEventFromServer(String name) {
            this.name = name;
        }

        public TestEventFromServer() {
        }

        @Override
        public String toString() {
            return "TestEventFromServer{" + "name=" + name + '}';
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class TestEventFromClient {

        private String name;

        public TestEventFromClient(String name) {
            this.name = name;
        }

        public TestEventFromClient() {
        }

        @Override
        public String toString() {
            return "TestEventFromClient{" + "name=" + name + '}';
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
