/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.system.nebus.server;

import com.google.common.base.Charsets;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.App;
import net.alternativmud.framework.ExternalService;
import net.alternativmud.lib.NamingThreadFactory;
import net.alternativmud.lib.debug.BusLogger;
import net.alternativmud.logic.event.GameBusAcceptedEvent;
import net.alternativmud.system.remoteadmin.RemoteAdminServer;
import net.alternativmud.system.telnetserver.TelnetServer;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;

/**
 * Ten serwer tworzy dla każdego połączenia Szynę Wydarzeń (EventBus), która
 * następnie może być przechwycona przez odpowiednią klasę. Tutaj dla odmiany
 * używam gniazd NIO. Możesz sobie porównać trzy opcje tworzenia serwerów w
 * javie.
 *
 * TODO: Trzeba to przepisać na Netty, bo blokuje się przy różnowątkowym
 * odczycie i zapisie.
 *
 * @author jblew
 */
public class TCPEBusServer implements ExternalService {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    static {
        JSON_MAPPER.setSerializationConfig(JSON_MAPPER.getSerializationConfig().without(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS));
    }
    private final Map<Channel, ChannelBus> buses = Collections.synchronizedMap(new HashMap<Channel, ChannelBus>());
    private final ExecutorService busExecutor = Executors.newCachedThreadPool(new NamingThreadFactory("TCP-EBusServer-bus-reader"));
    private final EventBus globalEBus;
    private ServerBootstrap tcpBootstrap;

    public TCPEBusServer(EventBus globalEBus) {
        this.globalEBus = globalEBus;
    }

    public void start() throws IOException {
        tcpBootstrap = new ServerBootstrap( //Tworzymy serwer
                new NioServerSocketChannelFactory( //Ta klasa mówi, że będzie używał Socketów NIO
                Executors.newCachedThreadPool(), //Dodajemy dwie automatyczne pule wątków
                Executors.newCachedThreadPool()));

        // Set up the pipeline factory.
        tcpBootstrap.setPipelineFactory(new ChannelPipelineFactory() {//Ta anonimowa klasa jest odpowiedzialna za tworzenie nowych gniazd

            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline(); //Tworzymy nowe gniazdo
                pipeline.addLast("framer", new LengthFieldBasedFrameDecoder(8192, 0, 4, 0, 0));
                pipeline.addLast("handler", new Handler());//Handler jest tworzony dla każdego nowego wątku i obsługuje go potem.
                return pipeline;
            }
        });

        tcpBootstrap.setOption("reuseAddress", true);

        tcpBootstrap.bind(new InetSocketAddress("0.0.0.0", getPort()));

        /*
         * SocketChannel userChannel = serverChannel.accept(); ChannelBus c =
         * new ChannelBus(userChannel); c.start(); buses.put(userChannel, c);
         */
    }

    public void stop() throws IOException {
        //System.out.println("TCPEBS.stop()");
        for (Channel chan : buses.keySet()) {
            ChannelBus bus = buses.get(chan);
            bus.stop();
            bus.close();
        }

        busExecutor.shutdown();
        boolean finished = false;
        try {
            finished = busExecutor.awaitTermination(150, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
        }
        if (!finished) {
            busExecutor.shutdownNow();
        }
        
        if (tcpBootstrap != null) {
            tcpBootstrap.releaseExternalResources();
            tcpBootstrap = null;
        }
    }

    @Override
    public int getPort() {
        return 8888;
    }

    @Override
    public boolean isBind() {
        return true;
    }

    @Override
    public String getDescription() {
        return "TCP EventBus Server";
    }
    
    @Override
    public ExternalService.ProtocolType getProtocolType() {
        return ExternalService.ProtocolType.TCP;
    }

    public Map<Channel, ChannelBus> getBuses() {
        return Collections.unmodifiableMap(buses);
    }

    private class Handler extends SimpleChannelUpstreamHandler {
        @Override
        public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            ChannelBus c = new ChannelBus(e.getChannel());
            c.start();
            buses.put(e.getChannel(), c);
            c.register(new StandardBusSubscriber(c));
            c.register(new BusLogger());
        }

        @Override
        public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            buses.get(e.getChannel()).close();
        }

        /**
         * Ta funkcja jest wykonywana, gdy user wyśle jakieś polecenie do
         * serwera.
         */
        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
            try {
                //System.out.println("SRV.MSG_RECV");
                ChannelBuffer buf = (ChannelBuffer) e.getMessage();
                int length = buf.readInt();//4
                byte[] classNameBytes = new byte[buf.readInt()];//4
                buf.readBytes(classNameBytes);
                String className = new String(classNameBytes, Charsets.UTF_8);
                
                byte[] bytes = new byte[length-classNameBytes.length-4];
                buf.readBytes(bytes);
                try {
                    Object o = JSON_MAPPER.readValue(bytes, Class.forName(className));
                    //System.out.println(o.getClass()+" o.toString(): "+o.toString());
                    buses.get(e.getChannel()).postOnlyOnLocalBus(o);
                } catch (ClassNotFoundException | IOException ex) {
                    Logger.getLogger(TCPEBusServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (Exception ex) {
                Logger.getLogger(TCPEBusServer.class.getName()).log(Level.SEVERE, "Exception in messageReceived", ex);
            }
        }

        /**
         * Ta metoda jest wykonywana podczas błędu.
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
            Logger.getLogger(RemoteAdminServer.class.getName()).log(Level.WARNING, "Unexpected exception from downstream.", e.getCause());
            e.getChannel().close();
        }

        @Override
        public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
            super.handleUpstream(ctx, e);
            //System.out.println("Event: " + e.toString() + " (" + e.getClass().getName() + ")");
        }

    }

    public class ChannelBus extends EventBus {

        private final Channel channel;

        private ChannelBus(Channel channel) {
            this.channel = channel;
            globalEBus.post(new EBusConnected(this, (InetSocketAddress) channel.getRemoteAddress()));
        }

        private void start() {
        }

        private void stop() {
            close();
        }

        /**
         * Posts only locally
         *
         * @param o
         */
        public void postOnlyOnLocalBus(Object o) {
            super.post(o);
        }

        @Override
        public void post(Object o) {
            postOnlyOnLocalBus(o);
            if (!(o instanceof DeadEvent)) {
                //System.out.println("Server.Sending(" + o.getClass().getName() + "): " + o.toString());
                try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                JSON_MAPPER.writeValue(baos, o);
                byte[] bytes = baos.toByteArray();
                byte [] classNameBytes = o.getClass().getName().getBytes();
                ChannelBuffer buf = ChannelBuffers.buffer(bytes.length + classNameBytes.length+8);
                buf.writeInt(buf.capacity()-4);
                buf.writeInt(classNameBytes.length);
                buf.writeBytes(classNameBytes);
                buf.writeBytes(bytes);
                //System.out.println("Written msg; contentLength="+bytes.length+"; classNameBytes.length="+classNameBytes.length
                //        +"; buf.capacity()"+buf.capacity());
                
                channel.write(buf);
            } catch (Exception ex) {
                Logger.getLogger(TCPEBusServer.class.getName()).log(Level.SEVERE, "Exception in server.post", ex);
            }
            }
        }

        private void close() {
            channel.close();
            EBusClosed evt = new EBusClosed(this, (InetSocketAddress) channel.getRemoteAddress());
            this.postOnlyOnLocalBus(evt);
            globalEBus.post(evt);
        }
    }

    public static class EBusConnected {

        public final EventBus ebus;
        public final InetSocketAddress addr;

        public EBusConnected(EventBus ebus, InetSocketAddress addr) {
            this.ebus = ebus;
            this.addr = addr;
        }
    }

    public static class EBusClosed {

        public final EventBus ebus;
        public final InetSocketAddress addr;

        public EBusClosed(EventBus ebus, InetSocketAddress addr) {
            this.ebus = ebus;
            this.addr = addr;
        }
    }
}
